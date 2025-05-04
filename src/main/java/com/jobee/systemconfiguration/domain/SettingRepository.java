package com.jobee.systemconfiguration.domain;

import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface SettingRepository extends CrudRepository<Setting, Long> {

    @NativeQuery("SELECT * " +
            "FROM [Setting] " +
            "FOR SYSTEM_TIME AS OF :time " +
            "WHERE [Context] = :context AND [Name] = :name ")
    Optional<Setting> find(LocalDateTime time, String context, String name);

    Optional<Setting> findByContextAndName(String context, String name);

    @NativeQuery("SELECT * " +
            "FROM [Setting] " +
            "FOR SYSTEM_TIME AS OF :time " +
            "WHERE [Context] = :context AND [Name] IN (:names) ")
    Collection<Setting> findAll(LocalDateTime time, String context, Collection<String> names);
}
