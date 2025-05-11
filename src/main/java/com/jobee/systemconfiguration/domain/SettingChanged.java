package com.jobee.systemconfiguration.domain;

import com.jobee.systemconfiguration.domain.annotations.Event;

@Event(name = "SETTING_VALUE_CHANGED")
public record SettingChanged(String context, String name, String oldValue, String newValue) {
}
