package com.example.trade.controller;

import com.example.trade.domain.TradeEvent;
import com.example.trade.producer.TradeEventProducer;
import com.example.trade.service.TradeEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trade-events")
@RequiredArgsConstructor
public class TradeEventController {

    private final TradeEventService service;
    private final TradeEventProducer tradeEventProducer;

    @PostMapping("/")
    public String create(@RequestBody TradeEvent event){
        tradeEventProducer.send(event);
        return "OK";
    }

    @GetMapping("/{id}")
    public TradeEvent get(@PathVariable String id){
        return service.get(id);
    }

    @GetMapping("/trade/{tradeId}")
    public List<TradeEvent> getByTradeId(@PathVariable String tradeId){
        return service.getByTradeId(tradeId);
    }
}
