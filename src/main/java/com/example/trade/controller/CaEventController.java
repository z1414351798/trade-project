package com.example.trade.controller;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.TradeEvent;
import com.example.trade.producer.CaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ca-events")
@RequiredArgsConstructor
public class CaEventController {
    private final CaEventProducer caEventProducer;

    @PostMapping("/")
    public String create(@RequestBody CaEvent event){
        caEventProducer.send(event);
        return "OK";
    }
}
