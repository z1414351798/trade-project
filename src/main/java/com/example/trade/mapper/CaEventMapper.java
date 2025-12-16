package com.example.trade.mapper;

import com.example.trade.domain.CaEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface CaEventMapper {
    void insert(CaEvent event);
    CaEvent findById(String id);
    List<CaEvent> findByInstrumentId(String instrumentId);
}
