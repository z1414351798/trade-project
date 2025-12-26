package com.example.trade.controller;

import com.example.trade.domain.Response;
import com.example.trade.domain.TradeEvent;
import com.example.trade.producer.TradeEventProducer;
import com.example.trade.service.TradeEventService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeEventController {

    private final TradeEventService service;
    private final TradeEventProducer tradeEventProducer;

    @PostMapping("/")
    public String create(@RequestBody TradeEvent event){
        tradeEventProducer.send(event);
        return "OK";
    }

    @GetMapping("/getByTradeId/{tradeId}")
    public Response<TradeEvent> getByTradeId(@PathVariable String tradeId){
        return service.getByTradeId(tradeId);
    }

    @GetMapping("/getByAccountId/{accountId}")
    public Response<List<TradeEvent>> getByAccountId(@PathVariable String accountId){
        return service.getByAccountId(accountId);
    }

    @GetMapping("/getByInstrumentId/{instrumentId}")
    public Response<List<TradeEvent>> getByInstrumentId(@PathVariable String instrumentId){
        return service.getByInstrumentId(instrumentId);
    }

    @PostMapping("/updateStatus")
    public Response<String> updateStatus(@RequestParam String tradeId, @RequestParam String status){
        try {
            return service.updateStatus(tradeId,status);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
