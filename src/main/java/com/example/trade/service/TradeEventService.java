package com.example.trade.service;

import com.example.trade.domain.Response;
import com.example.trade.domain.TradeEvent;
import com.example.trade.mapper.TradeEventDedupMapper;
import com.example.trade.mapper.TradeEventMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.trade.domain.TradeEventDedup;

import java.util.ConcurrentModificationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeEventService {

    private final TradeEventMapper tradeEventMapper;
    private final TradeEventDedupMapper dedupMapper;
    private final RedissonClient redissonClient;
    private final CacheService cacheService;

    private final String tradeIdPreKey = "trade:id:";
    private final String tradeAccountIdPreKey = "trade:account:id:";
    private final String tradeInstrumentIdPreKey = "trade:instrument:id:";

    @Transactional
    public Response<TradeEvent> consume(TradeEvent event,
                            String topic,
                            int partition,
                            long offset) {

        try {
            dedupMapper.insert(
                    TradeEventDedup.of(event, topic, partition, offset)
            );

        } catch (DuplicateKeyException e) {
            // 已处理过，直接返回
            return Response.error("Duplicate trade event dedup");
        }

        event.setVersion(1);

        // 真正的业务写入
        int insertOk = tradeEventMapper.insert(event);
        if (insertOk == 0 ){
            return Response.error("Duplicate trade event");
        }
        // 设置 version，避免未来读旧数据
        cacheService.set(tradeIdPreKey + event.getTradeId() + ":version",1,24);
        boolean deleteCache = cacheService.delete(tradeIdPreKey + event.getTradeId());
        if (!deleteCache){
            System.out.println("Cache not exist");
        }
        return Response.success(event);
    }

    public Response<TradeEvent> getByTradeId(String id){
        String key = tradeIdPreKey + id;
        // 热点阈值 1000，缓存逻辑全部交给 CacheService
        TradeEvent tradeEvent = cacheService.get(key, () -> tradeEventMapper.findByTradeId(id), 1000);
        return Response.success(tradeEvent);
    }

    public Response<List<TradeEvent>> getByAccountId(String accountId){
        String key = tradeAccountIdPreKey + accountId;
        // 热点阈值 1000，缓存逻辑全部交给 CacheService
        List<TradeEvent> tradeEvents = cacheService.get(key, () -> tradeEventMapper.findByAccountId(accountId), 1000);
        return  Response.success(tradeEvents);
    }

    public Response<List<TradeEvent>> getByInstrumentId(String instrumentId){
        String key = tradeInstrumentIdPreKey + instrumentId;
        // 热点阈值 1000，缓存逻辑全部交给 CacheService
        List<TradeEvent> tradeEvents = cacheService.get(key, () -> tradeEventMapper.findByInstrumentId(instrumentId), 1000);
        return Response.success(tradeEvents);
    }

    @Transactional
    public Response<String> updateStatus(String tradeId,String status) throws NotFoundException {
        String key = tradeIdPreKey + tradeId;
        String versionKey = tradeIdPreKey + tradeId + ":version";
        TradeEvent byTradeId = tradeEventMapper.findByTradeId(tradeId);
        if (byTradeId == null) {
            throw new NotFoundException("TradeEvent not exists");
        }
        int updateStatus = tradeEventMapper.updateStatus(tradeId, status, byTradeId.getVersion());
        if (updateStatus == 0){
            throw new ConcurrentModificationException("TradeEvent updated by others");
        }

        cacheService.set(versionKey,byTradeId.getVersion()+1,24);

        boolean deleteCache = cacheService.delete(key);
        if (!deleteCache){
            System.out.println("Cache not exist");
        }
        return Response.success("Update trade status success");

    }
}
