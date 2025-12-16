package com.example.trade.mapper;

import com.example.trade.domain.DerivedTradeEvent;
import com.example.trade.domain.DerivedTradeEventDedup;

public interface DerivedTradeEventDedupMapper {
    int insert(DerivedTradeEventDedup derivedTradeEventDedup);
}
