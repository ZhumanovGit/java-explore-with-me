package ru.practicum.client;

import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatRequest;

import java.util.List;

public interface StatClient {
    void postHit(CreatingStatDto dto);

    List<StatDto> get(StatRequest request);
}
