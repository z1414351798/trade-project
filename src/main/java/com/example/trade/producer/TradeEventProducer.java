package com.example.trade.producer;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.TradeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeEventProducer {

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;

    public void send(TradeEvent event) {
        kafkaTemplate.send("trade-event", event.getEventId(), event);
    }
}
