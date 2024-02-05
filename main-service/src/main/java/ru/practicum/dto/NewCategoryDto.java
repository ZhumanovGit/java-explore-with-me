package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@Jacksonized
public class NewCategoryDto {
    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 1, max = 50, message = "Field: name. Error: length must be at least 1 and at most 50")
    private final String name;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public NewCategoryDto(String name) {
        this.name = name;
    }
}
