package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.entity.ParticipantRequest;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    ParticipationRequestDto requestToRequestDto(ParticipantRequest request);
}
