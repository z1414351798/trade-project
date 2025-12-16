package com.example.trade.consumer;

import com.example.trade.domain.TradeEvent;
import com.example.trade.service.TradeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeEventConsumer {

    private final TradeEventService service;
    private final ExecutorService tradeWorkerExecutor;

    @KafkaListener(topics = "trade-event", groupId = "trade-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(
            TradeEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, TradeEvent> record) {

        tradeWorkerExecutor.submit(() -> {
            service.consume(
                    event,
                    record.topic(),
                    record.partition(),
                    record.offset()
            );
            ack.acknowledge(); // 业务成功后再 commit offset
        });

    }
}
