package com.example.trade.consumer;

import com.example.trade.domain.DerivedTradeEvent;
import com.example.trade.domain.TradeEvent;
import com.example.trade.service.DerivedTradeEventService;
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
public class DerivedTradeEventConsumer {

    private final DerivedTradeEventService service;
    private final ExecutorService derivedTradeWorkerExecutor;

    @KafkaListener(topics = "derived-trade-event", groupId = "derived-trade-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void retry(
            DerivedTradeEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, DerivedTradeEvent> record) {

        derivedTradeWorkerExecutor.submit(() -> {
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
