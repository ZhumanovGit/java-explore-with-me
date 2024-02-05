package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatRequest;

import java.util.List;

public interface StatClient {
    void postHit(CreatingStatDto dto);

    ResponseEntity<List<StatDto>> get(StatRequest request);
}
