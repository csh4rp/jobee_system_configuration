package com.jobee.systemconfiguration.contracts;

import com.jobee.systemconfiguration.contracts.annotations.Event;

import java.time.Clock;
import java.time.LocalDateTime;

@Event(name = "setting-changed", topic = "settings")
public record SettingChangedEvent(String context, String name, String value, LocalDateTime timestamp) implements IntegrationEvent {

    public SettingChangedEvent(String context, String name, String value) {
        this(context, name, value, LocalDateTime.now(Clock.systemUTC()));
    }
}
