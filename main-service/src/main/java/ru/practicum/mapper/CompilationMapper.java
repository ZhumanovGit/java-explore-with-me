package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.entity.Compilation;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    Compilation newCompilationDtoToCompilation(NewCompilationDto dto);

    CompilationDto compilationToCompilationDto(Compilation compilation);
}
