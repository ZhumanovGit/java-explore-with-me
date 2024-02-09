package ru.practicum.entity.enums;

import java.util.Optional;

public enum StateStatus {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static Optional<StateStatus> from(String stringState) {
        for (StateStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
