package com.example.trade.consumer;

import com.example.trade.domain.CaEvent;
import com.example.trade.domain.TradeEvent;
import com.example.trade.service.CaEventService;
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
public class CaEventConsumer{

    private final CaEventService service;
    private final ExecutorService caWorkerExecutor;

    @KafkaListener(topics = "ca-event", groupId = "ca-event-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(
            CaEvent event,
            Acknowledgment ack,
            ConsumerRecord<String, CaEvent> record) {

        caWorkerExecutor.submit(() -> {
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
