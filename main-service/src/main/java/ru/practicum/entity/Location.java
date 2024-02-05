package ru.practicum.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class Location {
    @NotNull
    private final Float lat;
    @NotNull
    private final Float lon;
}
