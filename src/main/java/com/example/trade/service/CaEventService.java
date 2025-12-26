package com.example.trade.service;

import com.example.trade.domain.*;
import com.example.trade.mapper.CaEventDedupMapper;
import com.example.trade.mapper.CaEventMapper;
import com.example.trade.mapper.TradeEventDedupMapper;
import com.example.trade.mapper.TradeEventMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaEventService {

    private final CaEventMapper caEventMapper;

    private final CaEventDedupMapper dedupMapper;
    private final RedissonClient redissonClient;
    private final CacheService cacheService;

    private final String caEventIdPreKey = "ca:event:id:";
    private final String caEventInstrumentIdPreKey = "ca:event:instrument:id:";

    @Transactional
    public Response<CaEvent> consume(CaEvent event,
                        String topic,
                        int partition,
                        long offset) {

        try {
            dedupMapper.insert(
                    CaEventDedup.of(event, topic, partition, offset)
            );
        } catch (DuplicateKeyException e) {
            // 已处理过，直接返回
            return Response.error("Duplicate ca event dedup");
        }

        event.setVersion(1);

        // 真正的业务写入
        int insertOk = caEventMapper.insert(event);
        if (insertOk == 0 ){
            return Response.error("Duplicate trade event");
        }
        // 设置 version，避免未来读旧数据
        redissonClient.getBucket(caEventIdPreKey + event.getCaEventId() + ":version")
                .set(event.getVersion());
        boolean deleteCache = cacheService.delete(caEventIdPreKey + event.getCaEventId());
        if (!deleteCache){
            System.out.println("Cache not exist");
        }
        return Response.success(event);

    }

    public Response<CaEvent> get(String id){
        String key = caEventIdPreKey + id;
        // 热点阈值 1000，缓存逻辑全部交给 CacheService
        CaEvent caEvent = cacheService.get(key, () -> caEventMapper.findById(id), 1000);
        return Response.success(caEvent);
    }

    public Response<List<CaEvent>> getByInstrumentId(String instrumentId){
        String key = caEventInstrumentIdPreKey + instrumentId;
        // 热点阈值 1000，缓存逻辑全部交给 CacheService
        List<CaEvent> caEvents = cacheService.get(key, () -> caEventMapper.findByInstrumentId(instrumentId), 1000);
        return Response.success(caEvents);
    }

    @Transactional
    public Response<String> updateStatus(String caEventId,String status) throws NotFoundException {
        String key = caEventIdPreKey + caEventId;
        String versionKey = caEventIdPreKey + caEventId + ":version";
        CaEvent caEvent = caEventMapper.findById(caEventId);
        if (caEvent == null) {
            throw new NotFoundException("caEvent not exists");
        }
        int updateStatus = caEventMapper.updateStatus(caEventId, status, caEvent.getVersion());
        if (updateStatus == 0){
            throw new ConcurrentModificationException("caEvent updated by others");
        }

        cacheService.set(versionKey,caEvent.getVersion()+1,24);

        boolean deleteCache = cacheService.delete(key);
        if (!deleteCache){
            System.out.println("Cache not exist");
        }
        return Response.success("Update caEvent status success");

    }
}
