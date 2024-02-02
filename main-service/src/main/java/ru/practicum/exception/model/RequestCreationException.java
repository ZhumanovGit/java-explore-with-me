package ru.practicum.exception.model;

public class RequestCreationException extends RuntimeException {
    public RequestCreationException(String message) {
        super(message);
    }
}
