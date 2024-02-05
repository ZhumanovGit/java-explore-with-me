package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class StatDto {
    private String app;
    private String uri;
    private Long hits;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public StatDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
