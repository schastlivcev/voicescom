package ru.kpfu.itis.voicescom.handlers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.kpfu.itis.voicescom.dto.ErrorDto;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        if(e instanceof HttpRequestMethodNotSupportedException) {
            return ResponseEntity.status(405).body(new ErrorDto(
                    messageSource.getMessage("global.not-supported", new Object[]{req.getMethod()}, req.getLocale()) ));
        }
        if(e instanceof AccessDeniedException) {
            return ResponseEntity.status(403).body(new ErrorDto(
                    messageSource.getMessage("global.access-denied", null, req.getLocale()) ));
        }

        log.error("Uncaught exception occurred on request:" + req.getRequestURI(), e);
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        return ResponseEntity.status(500).body(new ErrorDto(
                messageSource.getMessage("global.error", null, req.getLocale()) ));
    }
}
