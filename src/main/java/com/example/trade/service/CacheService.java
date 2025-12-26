package com.example.trade.service;

import com.example.trade.domain.Versionable;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedissonClient redissonClient;

    /**
     * 通用缓存查询方法
     * @param key Redis key
     * @param dataLoader 数据加载器（DB 查询逻辑）
     * @param hotThreshold 热点 Key 阈值，如果访问次数超过该值则加锁
     * @param <T> 缓存对象类型
     * @return 返回缓存或 DB 查询结果
     */
    public <T> T get(String key, Supplier<T> dataLoader, long hotThreshold) {
        RBucket<T> bucket = redissonClient.getBucket(key);

        // 1️⃣ 尝试从缓存获取
        T value = bucket.get();
        if (value != null) return value;

        // 2️⃣ 判断是否为热点 Key
        boolean isHot = false;
        if (hotThreshold > 0) {
            RAtomicLong counter = redissonClient.getAtomicLong("access:" + key);
            long count = counter.incrementAndGet();
            counter.expire(1, TimeUnit.MINUTES);
            isHot = count > hotThreshold;
        }

        if (isHot) {
            // 3️⃣ 热点 Key 加分布式锁
            RLock lock = redissonClient.getLock("lock:" + key);
            try {
                if (lock.tryLock(100, 2000, TimeUnit.MILLISECONDS)) {
                    // double check
                    value = bucket.get();
                    if (value == null) {
                        value = loadAndCache(bucket, dataLoader, key);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        } else {
            // 4️⃣ 非热点 Key 直接加载并缓存
            value = loadAndCache(bucket, dataLoader, key);
        }

        return value;
    }

    /**
     * 缓存数据到 Redis，自动处理穿透和雪崩
     */
    private <T> T loadAndCache(RBucket<T> bucket, Supplier<T> dataLoader, String key) {
        T value = dataLoader.get();

        if (value == null || (value instanceof List && ((List<?>) value).isEmpty())) {
            // 空值缓存，防穿透
            bucket.set((T) Collections.emptyList(),
                    60 + new Random().nextInt(60), TimeUnit.SECONDS);
            return value;
        }

        // ==============================
        // 这里只对 "单条数据" 做 version 校验
        // 列表直接缓存，不校验 version
        // ==============================
        if (!(value instanceof List)) {

            // 如果是单条订单（实现了 Versionable）
            if (value instanceof Versionable vo) {
                String versionKey = key + ":version";
                RBucket<Integer> versionBucket = redissonClient.getBucket(versionKey);
                Integer currentVersion = versionBucket.get();

                if (currentVersion != null && vo.getVersion() < currentVersion) {
                    // 不写旧缓存
                    return value;
                }

            }
        }

        // 正常数据缓存（无论是单条还是列表）随机ttl防雪崩
        bucket.set(value, 300 + new Random().nextInt(300), TimeUnit.SECONDS);

        return value;
    }


    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }

    /**
     * 手动设置缓存
     */
    public <T> void set(String key, T value, long timeoutHours) {
        redissonClient.getBucket(key).set(value, timeoutHours, TimeUnit.HOURS);
    }
}
