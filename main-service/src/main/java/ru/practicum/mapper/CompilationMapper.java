package ru.practicum.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.entity.Compilation;

import java.util.HashSet;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto dto);

    CompilationDto compilationToCompilationDto(Compilation compilation);

    @BeforeMapping
    default void fillNeedAttributes(NewCompilationDto dto, @MappingTarget Compilation compilation) {
        if (dto.getPinned() == null) {
            compilation.setPinned(false);
        }
        compilation.setEvents(new HashSet<>());
    }
}
