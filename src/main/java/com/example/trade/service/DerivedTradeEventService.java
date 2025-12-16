package com.example.trade.service;

import com.example.trade.domain.DerivedTradeEvent;
import com.example.trade.domain.DerivedTradeEventDedup;
import com.example.trade.mapper.DerivedTradeEventDedupMapper;
import com.example.trade.mapper.DerivedTradeEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DerivedTradeEventService {

    private final DerivedTradeEventMapper derivedTradeEventMapper;

    private final DerivedTradeEventDedupMapper dedupMapper;

    @Transactional
    public void consume(DerivedTradeEvent event,
                        String topic,
                        int partition,
                        long offset) {

        try {
            dedupMapper.insert(
                    DerivedTradeEventDedup.of(event, topic, partition, offset)
            );
        } catch (DuplicateKeyException e) {
            // 已处理过，直接返回
            return;
        }

        // 真正的业务写入
        derivedTradeEventMapper.insert(event);
    }

    public DerivedTradeEvent get(String id){
        return derivedTradeEventMapper.findById(id);
    }

    public List<DerivedTradeEvent> getByTradeId(String tradeId){
        return derivedTradeEventMapper.findByTradeId(tradeId);
    }
}
