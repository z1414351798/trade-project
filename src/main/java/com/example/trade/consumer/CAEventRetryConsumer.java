package com.example.trade.consumer;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.TradeEvent;
import com.example.trade.service.CaEventService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CAEventRetryConsumer {

    private final CaEventService service;

    @KafkaListener(
        topics = "ca-event-retry",
        groupId = "ca-event-retry-group",
        containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void listen(
            CaEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, CaEvent> record) {

            service.consume(
                    event,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );

            ack.acknowledge();

    }
}
