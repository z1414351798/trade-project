package com.example.trade.mapper;

import com.example.trade.domain.CaEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CaEventMapper {
    int insert(CaEvent event);
    CaEvent findById(String id);
    List<CaEvent> findByInstrumentId(String instrumentId);
    int updateStatus(@Param("caEventId") String caEventId, @Param("status") String status, @Param("version") int version);
}
