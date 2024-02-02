package ru.practicum.exception.model;

public class RequestModerationException extends RuntimeException {
    public RequestModerationException(String message) {
        super(message);
    }
}
