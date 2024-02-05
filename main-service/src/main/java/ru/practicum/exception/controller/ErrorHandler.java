package ru.practicum.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.ApiError;
import ru.practicum.exception.model.DataConnectivityException;
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

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("NotFoundException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("Data was not found")
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(EventModerationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventModerationException(final EventModerationException e) {
        log.warn("EventModerationException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Problem with moderation of Event")
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
                .reason("Error with request moderation")
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("IllegalArgumentException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Wrong data parameters")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataConnectivityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConnectivityException(final DataConnectivityException e) {
        log.warn("DataConnectivityException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("This object cant be deleted cause of connects to another data")
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
