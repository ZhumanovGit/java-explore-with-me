package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.entity.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class NewEventDto {
    @NotBlank(message = "Field: annotation. Error: annotation must not be blank")
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: annotation length must be at least 20, at most 2000")
    private String annotation;
    @NotNull(message = "Field: category. Error: category must not be null")
    private Long category;
    @NotBlank(message = "Field: description. Error: description must not be blank")
    @Size(min = 20, max = 7000, message = "Field: description. Error: description length must be at least 20, at most 7000")
    private String description;
    @Future(message = "Field: eventDate. Error: eventDate must be in future")
    private LocalDateTime eventDate;
    @NotNull(message = "Field: location. Error: location must not be null")
    private Location location;
    private Boolean paid;
    @PositiveOrZero(message = "Field: participantLimit. Error: participantLimit must not be negative number")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Field: title. Error: title must not be blank")
    @Size(min = 3, max = 120, message = "Field: title. Error: title length must be at least 3, at most 120")
    private String title;


}
