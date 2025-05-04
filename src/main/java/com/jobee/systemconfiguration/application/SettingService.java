package com.jobee.systemconfiguration.application;

import com.jobee.systemconfiguration.application.caching.SettingCacheService;
import com.jobee.systemconfiguration.application.exceptions.ConflictException;
import com.jobee.systemconfiguration.application.exceptions.EntityNotFoundException;
import com.jobee.systemconfiguration.application.messaging.MessagingService;
import com.jobee.systemconfiguration.contracts.SettingChangedEvent;
import com.jobee.systemconfiguration.contracts.SettingModel;
import com.jobee.systemconfiguration.domain.Setting;
import com.jobee.systemconfiguration.domain.SettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class SettingService {

    private final SettingRepository settingRepository;
    private final SettingCacheService settingCacheService;
    private final MessagingService messagingService;

    public SettingService(SettingRepository settingRepository, SettingCacheService settingCacheService, MessagingService messagingService) {
        this.settingRepository = settingRepository;
        this.settingCacheService = settingCacheService;
        this.messagingService = messagingService;
    }

    public SettingModel getSetting(String context, String name, LocalDateTime time) {

        LocalDateTime settingTime = time;

        if (time == null) {
            Optional<String> cachedValue = settingCacheService.getSetting(context, name);

            if (cachedValue.isPresent()) {
                return new SettingModel(context, name, cachedValue.get());
            }

            settingTime = LocalDateTime.now(Clock.systemUTC());
        }

        Setting setting = settingRepository.find(settingTime, context, name)
                .orElseThrow(EntityNotFoundException::new);

        if (time == null) {
            settingCacheService.setSetting(setting.getContext(), setting.getName(), setting.getValue());
        }

        return new SettingModel(setting.getContext(), setting.getName(), setting.getValue());
    }

    public void create(SettingModel model) {

        Optional<Setting> existingSetting = settingRepository.findByContextAndName(model.context(), model.name());

        if (existingSetting.isPresent()) {
            throw new ConflictException(String.format("Setting '%s:%s' already exists", model.context(), model.name()));
        }

        Setting setting = new Setting(model.context(), model.name(), model.value(), "");

        settingRepository.save(setting);

        log.info("Created setting '{}:{}'", model.context(), model.name());
    }

    public void update(SettingModel model) {

        Setting setting = settingRepository.findByContextAndName(model.context(), model.name())
                .orElseThrow(EntityNotFoundException::new);

        if (model.value().equals(setting.getValue())) {
            log.info("Setting '{}:{}' was not updated, because value of: '{}' matches old value", model.context(), model.name(), model.value());
            return;
        }

        setting.setValue(model.value());
        messagingService.publish(new SettingChangedEvent(setting.getContext(), setting.getName(), setting.getValue()));

        log.info("Updated setting '{}:{}' with value '{}'", model.context(), model.name(), model.value());
    }

    public Collection<SettingModel> getSettings(String context, Collection<String> names, LocalDateTime time) {

        Collection<Setting> settings = settingRepository.findAll(time, context, names);

        for (String setting : names) {
            if (settings.stream().noneMatch(s -> s.getName().equalsIgnoreCase(setting))) {
                throw new EntityNotFoundException(String.format("Setting '%s:%s' not found", context, setting));
            }
        }

        return settings.stream()
                .map(s -> new SettingModel(s.getContext(), s.getName(), s.getValue()))
                .toList();
    }

}
