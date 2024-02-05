package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Field: title. Error: title length must be at least 1, at most 50")
    private String title;
}
