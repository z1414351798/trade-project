package com.example.trade.controller;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.Response;
import com.example.trade.domain.TradeEvent;
import com.example.trade.producer.CaEventProducer;
import com.example.trade.service.CaEventService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ca-event")
@RequiredArgsConstructor
public class CaEventController {
    private final CaEventProducer caEventProducer;
    private final CaEventService caEventService;

    @PostMapping("/")
    public String create(@RequestBody CaEvent event){
        caEventProducer.send(event);
        return "OK";
    }

    @GetMapping("/getByEventId/{caEventId}")
    public Response<CaEvent> getByEventId(@PathVariable String caEventId){
        return caEventService.get(caEventId);
    }

    @GetMapping("/getByInstrumentId/{instrumentId}")
    public Response<List<CaEvent>> getByInstrumentId(@PathVariable String instrumentId){
        return caEventService.getByInstrumentId(instrumentId);
    }

    @PostMapping("/updateStatus")
    public Response<String> updateStatus(@RequestParam String tradeId, @RequestParam String status){
        try {
            return caEventService.updateStatus(tradeId,status);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
