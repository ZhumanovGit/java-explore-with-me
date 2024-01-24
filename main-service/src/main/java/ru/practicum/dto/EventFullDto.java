package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.entity.Location;
import ru.practicum.entity.StateStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFullDto {
    private final String annotation;
    private final CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private final LocalDateTime eventDate;
    private Long id;
    private final UserShortDto initiator;
    private final Location location;
    private final Boolean paid;
    @Builder.Default
    private Integer participantLimit = 0;
    private LocalDateTime publishedOn;
    @Builder.Default
    private Boolean requestModeration = true;
    private StateStatus state;
    private final String title;
    private Long views;
}
