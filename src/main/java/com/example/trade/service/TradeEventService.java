package com.example.trade.service;

import com.example.trade.domain.TradeEvent;
import com.example.trade.mapper.TradeEventDedupMapper;
import com.example.trade.mapper.TradeEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.trade.domain.TradeEventDedup;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeEventService {

    private final TradeEventMapper tradeEventMapper;

    private final TradeEventDedupMapper dedupMapper;

    @Transactional
    public void consume(TradeEvent event,
                        String topic,
                        int partition,
                        long offset) {

        try {
            dedupMapper.insert(
                    TradeEventDedup.of(event, topic, partition, offset)
            );

        } catch (DuplicateKeyException e) {
            // 已处理过，直接返回
            return;
        }

        // 真正的业务写入
        tradeEventMapper.insert(event);
    }

    public TradeEvent get(String id){
        return tradeEventMapper.findById(id);
    }

    public List<TradeEvent> getByTradeId(String tradeId){
        return tradeEventMapper.findByTradeId(tradeId);
    }
}
