package com.example.trade.consumer;

import com.example.trade.domain.DerivedTradeEvent;
import com.example.trade.domain.TradeEvent;
import com.example.trade.service.DerivedTradeEventService;
import com.example.trade.service.TradeEventService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DerivedTradeEventRetryConsumer {

    private final DerivedTradeEventService service;

    @KafkaListener(
        topics = "derived_trade-event-retry",
        groupId = "derived_trade-event-retry-group",
        containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void retry(
            DerivedTradeEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, DerivedTradeEvent> record) {

            service.consume(
                    event,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );

            ack.acknowledge();

    }
}
