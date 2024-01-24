package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
