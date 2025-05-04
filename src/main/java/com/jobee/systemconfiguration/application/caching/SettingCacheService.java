package com.jobee.systemconfiguration.application.caching;

import java.util.Optional;

public interface SettingCacheService {
    Optional<String> getSetting(String context, String name);

    void setSetting(String context, String name, String value);
}
