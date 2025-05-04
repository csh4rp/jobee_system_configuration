package com.jobee.systemconfiguration.api.handlers;

import com.jobee.systemconfiguration.application.exceptions.ConflictException;
import com.jobee.systemconfiguration.application.exceptions.EntityNotFoundException;
import com.jobee.systemconfiguration.application.exceptions.TechnicalException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public void handleNotFoundException(HttpServletResponse response, EntityNotFoundException ignoredEx) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public void handleNotFoundException(HttpServletResponse response, ConflictException ignoredEx) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @ExceptionHandler(TechnicalException.class)
    public void handleTechnicalException(HttpServletResponse response, TechnicalException ex) {

        log.error("Technical exception occurred", ex);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
