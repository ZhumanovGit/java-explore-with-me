package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EventShortDto {
    private final String annotation;
    private final CategoryDto category;
    private Long confirmedRequests;
    private final LocalDateTime eventDate;
    private Long id;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private Long views;
}
