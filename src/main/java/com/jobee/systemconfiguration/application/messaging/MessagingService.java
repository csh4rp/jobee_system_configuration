package com.jobee.systemconfiguration.application.messaging;

public interface MessagingService {

    <T> void publish(T event);
}
