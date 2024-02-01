package ru.practicum.exception.model;

public class EventModificationException extends RuntimeException {
    public EventModificationException(String message) {
        super(message);
    }
}
