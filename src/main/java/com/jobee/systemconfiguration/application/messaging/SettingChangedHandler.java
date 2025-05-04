package com.jobee.systemconfiguration.application.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobee.systemconfiguration.application.exceptions.EntityNotFoundException;
import com.jobee.systemconfiguration.contracts.SettingChangedEvent;
import com.jobee.systemconfiguration.domain.Setting;
import com.jobee.systemconfiguration.domain.SettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SettingChangedHandler {

    private final ObjectMapper objectMapper;
    private final SettingRepository settingRepository;

    public SettingChangedHandler(ObjectMapper objectMapper, SettingRepository settingRepository) {
        this.objectMapper = objectMapper;
        this.settingRepository = settingRepository;
    }

    @KafkaListener(id = "setting-changed-listener", topics = "${spring.kafka.topic-name}", clientIdPrefix = "setting-changed-listener")
    public void listen(String data) throws JsonProcessingException {

        try {

            SettingChangedEvent event = objectMapper.readValue(data, SettingChangedEvent.class);

            Setting setting = settingRepository.findByContextAndName(event.context(), event.name())
                    .orElseThrow(EntityNotFoundException::new);

            setting.setValue(event.value());

            settingRepository.save(setting);

            log.info("Updated setting '{}' with value '{}'", event.name(), event.value());
        }
        catch (JsonProcessingException e) {
            log.error("Failed to deserialize event: {}", data, e);
            throw e;
        }
    }
}
