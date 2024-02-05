package ru.practicum.exception.model;

public class DataConnectivityException extends RuntimeException {
    public DataConnectivityException(String message) {
        super(message);
    }
}
