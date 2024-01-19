package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StatRequest {
    private String start;
    private String end;
    private List<String> uris;
    private Boolean unique;
}
