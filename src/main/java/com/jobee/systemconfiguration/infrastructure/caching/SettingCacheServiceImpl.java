package com.jobee.systemconfiguration.infrastructure.caching;

import com.jobee.systemconfiguration.application.caching.SettingCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class SettingCacheServiceImpl implements SettingCacheService {

    private final RedisCacheManager cacheManager;

    public SettingCacheServiceImpl(RedisCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Optional<String> getSetting(String context, String name) {
        Cache cache = this.cacheManager.getCache("settings");

        if (cache == null) {
            throw new IllegalStateException("Cache 'settings' not found");
        }

        String key = getCacheKey(context, name);
        String value = cache.get(key, String.class);

        if (value == null || value.isEmpty()) {
            log.trace("Setting not found for context '{}' and name '{}'", context, name);
            return Optional.empty();
        }

        return Optional.of(value);
    }

    @Override
    public void setSetting(String context, String name, String value) {
        Cache cache = this.cacheManager.getCache("settings");

        if (cache == null) {
            throw new IllegalStateException("Cache 'settings' not found");
        }

        String key = getCacheKey(context, name);
        cache.put(key, value);
    }

    private static String getCacheKey(String context, String name) {
        return String.format("%s:%s", context.toLowerCase(Locale.ROOT), name.toLowerCase(Locale.ROOT));
    }

}
