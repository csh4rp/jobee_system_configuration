package com.jobee.systemconfiguration.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobee.systemconfiguration.application.messaging.MessagingService;
import com.jobee.systemconfiguration.application.exceptions.TechnicalException;
import com.jobee.systemconfiguration.contracts.annotations.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class MessagingServiceImpl implements MessagingService {

    @Value("${spring.kafka.number-of-partitions}")
    private int numberOfPartitions;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessagingServiceImpl(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public <T> void publish(T event) {
        Event eventAnnotation = event.getClass().getAnnotation(Event.class);

        if (eventAnnotation == null) {
            throw new IllegalStateException("Event class must be annotated with @Topic");
        }

        String name = eventAnnotation.name();
        String topic = eventAnnotation.topic();
        String value = serializeObject(event);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, getPartition(value), name, value);
        SendResult<String, String> result = sendMessage(record);

        log.info("Published setting change event to topic '{}'", result.getRecordMetadata().topic());
    }

    private SendResult<String, String> sendMessage(ProducerRecord<String, String> message) {
        try {
            int publishTimeoutInMs = 10000;
            return kafkaTemplate.send(message).get(publishTimeoutInMs, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new TechnicalException("Failed to send message", e);
        }
    }

    private int getPartition(String value) {
        return Math.abs(value.hashCode() % numberOfPartitions);
    }

    private <T> String serializeObject(T event) {
        try {
            return objectMapper.writeValueAsString(event);
        }
        catch (JsonProcessingException e) {
            throw new TechnicalException(String.format("Failed to serialize event: %s", event.toString()), e);
        }
    }
}
