package ru.practicum.client;

import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;

import java.util.List;
import java.util.Map;

public interface StatClient {
    void postHit(CreatingStatDto dto);

    List<StatDto> get(Map<String, Object> params);
}
