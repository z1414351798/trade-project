package com.example.trade.mapper;

import com.example.trade.domain.DerivedTradeEvent;

import java.util.List;

public interface DerivedTradeEventMapper {
    int insert(DerivedTradeEvent trade);
    DerivedTradeEvent findById (String derivedTradeId);
    List<DerivedTradeEvent> findByTradeId(String sourceTradeId);
    List<DerivedTradeEvent> findByCaEventId(String caEventId);
    List<DerivedTradeEvent> findByInstrumentId(String instrumentId);
}
