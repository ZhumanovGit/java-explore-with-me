package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank(message = "Field: title. Error: title must not be blank")
    @Size(min = 1, max = 50, message = "Field: title. Error: title length must be at least 1, at most 50")
    private final String title;
}
