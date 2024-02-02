package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewCategoryDto {
    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 1, max = 50, message = "Field: name. Error: length must be at least 1 and at most 50")
    private final String name;
}
