package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;
import ru.practicum.entity.Location;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@Validated
@Jacksonized
public class NewEventDto {
    @NotBlank(message = "Field: annotation. Error: annotation must not be blank")
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: annotation length must be at least 20, at most 2000")
    private String annotation;
    @NotNull(message = "Field: category. Error: category must not be null")
    private Long category;
    @NotBlank(message = "Field: description. Error: description must not be blank")
    @Size(min = 20, max = 7000, message = "Field: description. Error: description length must be at least 20, at most 7000")
    private String description;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "In format: yyyy-MM-dd HH:mm:ss")
    private String eventDate;
    @NotNull(message = "Field: location. Error: location must not be null")
    @Valid
    private Location location;
    private Boolean paid;
    @PositiveOrZero(message = "Field: participantLimit. Error: participantLimit must not be negative number")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Field: title. Error: title must not be blank")
    @Size(min = 3, max = 120, message = "Field: title. Error: title length must be at least 3, at most 120")
    private String title;


}
