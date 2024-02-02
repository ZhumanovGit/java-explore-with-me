package ru.practicum.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.exception.StatServiceException;
import ru.practicum.exception.model.ApiError;
import ru.practicum.exception.model.EventModerationException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.RequestModerationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice(basePackages = "ru.practicum")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("NotFoundException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason(e.getCause().toString())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidateException(final ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        ApiError error = ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .reason(e.getCause().toString())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        log.warn("ConstraintViolationExceptions, {}", errors);
        return error;
    }

    @ExceptionHandler(EventModerationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventModerationException(final EventModerationException e) {
        log.warn("EventModerationException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason(e.getCause().toString())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

    }

    @ExceptionHandler(RequestModerationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequestModerationException(final RequestModerationException e) {
        log.warn("RequestModerationException {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason(e.getCause().toString())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason(e.getCause().toString())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOtherExceptions(final Exception e) {
        log.warn("Внутреннее исключение {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason(printStackTrace(e))
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String printStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
