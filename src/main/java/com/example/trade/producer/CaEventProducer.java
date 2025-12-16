package com.example.trade.producer;

import com.example.trade.domain.CaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CaEventProducer {

    private final KafkaTemplate<String, CaEvent> kafkaTemplate;

    public void send(CaEvent event) {
        kafkaTemplate.send("ca-event", event.getCaEventId(), event);
    }
}
