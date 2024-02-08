package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.entity.Location;
import ru.practicum.entity.StateStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Jacksonized
public class EventFullDto {
    private final String annotation;
    private final CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    private Long id;
    private final UserShortDto initiator;
    private final Location location;
    private final Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private StateStatus state;
    private final String title;
    private Long views;
}
