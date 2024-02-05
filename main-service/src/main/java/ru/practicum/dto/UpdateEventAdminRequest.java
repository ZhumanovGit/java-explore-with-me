package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.entity.AdminStateAction;
import ru.practicum.entity.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "Field: annotation. Error: annotation length must be at least 20, at most 2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Field: annotation. Error: annotation length must be at least 20, at most 2000")
    private String description;
    @Future(message = "Field: eventDate. Error: eventDate must be in future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;
    @Size(min = 3, max = 120, message = "Field: title. Error: title length must be at least 3, at most 120")
    private String title;

}
