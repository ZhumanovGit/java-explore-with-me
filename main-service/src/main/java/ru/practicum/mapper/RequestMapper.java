package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.ParticipantRequest;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    ParticipationRequestDto requestToRequestDto(ParticipantRequest request);
}
