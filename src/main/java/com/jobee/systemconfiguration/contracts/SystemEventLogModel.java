package com.jobee.systemconfiguration.contracts;

import java.time.LocalDateTime;

public record SystemEventLogModel(LocalDateTime timestamp, String context, String action, String actor, String payload, String traceId) {

}
