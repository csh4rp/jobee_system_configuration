package com.jobee.systemconfiguration.contracts.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    String topic();

    String name();
}
