package ru.practicum.exception.model;

public class EventModerationException extends RuntimeException {
    public EventModerationException(String message) {
        super(message);
    }
}
