package com.example.trade.service;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.CaEventDedup;
import com.example.trade.domain.TradeEvent;
import com.example.trade.domain.TradeEventDedup;
import com.example.trade.mapper.CaEventDedupMapper;
import com.example.trade.mapper.CaEventMapper;
import com.example.trade.mapper.TradeEventDedupMapper;
import com.example.trade.mapper.TradeEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaEventService {

    private final CaEventMapper caEventMapper;

    private final CaEventDedupMapper dedupMapper;

    @Transactional
    public void consume(CaEvent event,
                        String topic,
                        int partition,
                        long offset) {

        try {
            dedupMapper.insert(
                    CaEventDedup.of(event, topic, partition, offset)
            );
        } catch (DuplicateKeyException e) {
            // 已处理过，直接返回
            return;
        }

        // 真正的业务写入
        caEventMapper.insert(event);
    }

    public CaEvent get(String id){
        return caEventMapper.findById(id);
    }

    public List<CaEvent> getByInstrumentId(String instrumentId){
        return caEventMapper.findByInstrumentId(instrumentId);
    }
}
