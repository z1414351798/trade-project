package com.example.trade.mapper;

import com.example.trade.domain.TradeEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface TradeEventMapper {
    void insert(TradeEvent event);
    TradeEvent findById(String eventId);
    List<TradeEvent> findByTradeId(String tradeId);
    List<TradeEvent> findByInstrumentId(String instrumentId);
    List<TradeEvent> findByAccountId(String accountId);
}
