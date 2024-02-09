package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventSubSearchRequest {
    private Long followerId;
    private Long publisherId;
    private Boolean onlyAvailable;
    private Boolean onlyFuture;
}
