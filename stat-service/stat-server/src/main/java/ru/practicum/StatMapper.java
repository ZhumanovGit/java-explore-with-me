package ru.practicum;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatMapper {
    public Stat creatingDtoToStat(CreatingStatDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Stat.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), formatter))
                .build();
    }

    public StatDto viewStatToStatDto(ViewStat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(stat.getHits())
                .build();
    }
}
