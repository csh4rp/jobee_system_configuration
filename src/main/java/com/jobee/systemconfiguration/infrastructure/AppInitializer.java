package com.jobee.systemconfiguration.infrastructure;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

@Service
public class AppInitializer {

    @Value("${spring.kafka.create-topics}")
    private boolean createTopics;

    @Value("${spring.kafka.topic-name}")
    private String settingsTopic;

    @Value("${spring.kafka.number-of-partitions}")
    private int numberOfPartitions;

    private final KafkaAdmin kafkaAdmin;

    public AppInitializer(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    public void initialize() {

        if (!createTopics){
            return;
        }

        NewTopic topic =  TopicBuilder.name(settingsTopic)
                .partitions(numberOfPartitions)
                .compact()
                .build();

        kafkaAdmin.createOrModifyTopics(topic);
    }
}
