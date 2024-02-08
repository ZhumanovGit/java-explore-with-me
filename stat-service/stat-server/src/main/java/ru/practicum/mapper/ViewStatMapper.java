package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.StatDto;
import ru.practicum.model.ViewStat;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewStatMapper {
    default StatDto mapToViewStatsDto(ViewStat viewStats) {
        if (viewStats == null) {
            return null;
        }
        return StatDto.builder()
                .app(viewStats.getApp())
                .hits(viewStats.getHits())
                .uri(viewStats.getUri())
                .build();
    }

    List<StatDto> mapToViewStatsDto(List<ViewStat> viewStats);
}
