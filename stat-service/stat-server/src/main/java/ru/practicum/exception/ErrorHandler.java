package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.ApiError;
import ru.practicum.exception.model.BadRequestException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        log.warn("BadRequestException, {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .reason("Bad request params")
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
