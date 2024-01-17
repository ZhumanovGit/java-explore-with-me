package ru.practicum.service;

import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void createNewStatRecord(CreatingStatDto dto);

    List<StatDto> getStats(LocalDateTime start,
                           LocalDateTime end,
                           List<String> uris,
                           Boolean unique);
}