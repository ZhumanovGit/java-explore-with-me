package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewUserRequest {
    @NotBlank(message = "Field: email. Error: email must not be null")
    @Email(message = "Field: email. Error: email must match the pattern")
    @Size(min = 6, max = 254, message = "Field: email. Error: email length must be at least 6, at most 254")
    private String email;
    @NotBlank(message = "Field: name. Error: name must not be blank")
    @Size(min = 2, max = 250, message = "Field: name. Error: name length must be at least 2, at most 250")
    private String name;
}
