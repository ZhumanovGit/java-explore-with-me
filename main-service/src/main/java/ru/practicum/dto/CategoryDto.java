package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 1, max = 50, message = "Field: name. Error: length must be at least 1 and at most 50")
    private final String name;
}
