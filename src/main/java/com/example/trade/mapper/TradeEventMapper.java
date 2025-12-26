package com.example.trade.mapper;

import com.example.trade.domain.TradeEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TradeEventMapper {
    int insert(TradeEvent event);
    TradeEvent findByTradeId(String tradeId);
    List<TradeEvent> findByInstrumentId(String instrumentId);
    List<TradeEvent> findByAccountId(String accountId);
    int updateStatus(@Param("tradeId") String tradeId, @Param("status") String status, @Param("version") int version);
}
