package com.example.trade.consumer;

import com.example.trade.domain.TradeEvent;
import com.example.trade.service.TradeEventService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeEventRetryConsumer {

    private final TradeEventService service;

    @KafkaListener(
        topics = "trade-event-retry",
        groupId = "trade-event-retry-group",
        containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void retry(
            TradeEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, TradeEvent> record) {

            service.consume(
                    event,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );

            ack.acknowledge();

    }
}
