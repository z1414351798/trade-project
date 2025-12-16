package com.example.trade.config;

import io.micrometer.core.instrument.config.validate.ValidationException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler commonErrorHandler(
            KafkaTemplate<String, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template, (record, ex) -> {

                    String sourceTopic = record.topic();

                    if (isRetryable(ex)) {
                        return new TopicPartition(
                                sourceTopic + "-retry",
                                record.partition()
                        );
                    }

                    return new TopicPartition(
                            sourceTopic + "-dlt",
                            record.partition()
                    );
                });

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3)
        );
    }

    private boolean isRetryable(Throwable ex) {

        Throwable root = getRootCause(ex);

        // 基础设施类（强烈 retry）
        if (root instanceof SQLException) return true;
        if (root instanceof TimeoutException) return true;
        if (root instanceof ConnectException) return true;

        // Kafka 可恢复异常
        if (root instanceof RetriableException) return true;

        // 业务明确不可恢复
        if (root instanceof IllegalArgumentException) return false;
        if (root instanceof ValidationException) return false;

        return false;
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate,
            DefaultErrorHandler commonErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.setBatchListener(false);

        factory.getContainerProperties().setAckMode(
                ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        factory.setCommonErrorHandler(commonErrorHandler);

        return factory;
    }

    @Bean
    public DefaultErrorHandler retryErrorHandler(
            KafkaTemplate<String, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template, (record, ex) ->
                        new TopicPartition(record.topic().replace("-retry", "-dlt"),
                                record.partition())
                );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(2000L, 3)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    retryKafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler retryErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(retryErrorHandler);

        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        factory.setConcurrency(1); // 限速

        return factory;
    }

}
