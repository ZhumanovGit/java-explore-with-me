package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatMapper {
    @Mapping(target = "timestamp", source = "dto.timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Stat dtoToStat(CreatingStatDto dto);

    StatDto statToStatDto(Stat stat);

    default StatDto viewStatToStatDto(ViewStat viewStats) {
        if (viewStats == null) {
            return null;
        }
        return StatDto.builder()
                .app(viewStats.getApp())
                .hits(viewStats.getHits())
                .uri(viewStats.getUri())
                .build();
    }

    List<StatDto> listViewStatToListDto(List<ViewStat> viewStats);
}
