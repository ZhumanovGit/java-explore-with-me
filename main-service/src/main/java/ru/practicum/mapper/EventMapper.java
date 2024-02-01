package ru.practicum.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.StateStatus;
import ru.practicum.entity.User;

import java.time.LocalDateTime;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "lat", source = "dto.location.lat")
    @Mapping(target = "lon", source = "dto.location.lon")
    @Mapping(target = "initiator.id", source = "user.id")
    @Mapping(target = "initiator.name", source = "user.name")
    @Mapping(target = "initiator.email", source = "user.email")
    @Mapping(target = "category", ignore = true)
    Event newEventDtoToEvent(NewEventDto dto, User user);

    @Mapping(target = "initiator.id", source = "initiator.id")
    @Mapping(target = "initiator.name", source = "initiator.name")
    EventShortDto eventToEventShortDto(Event event);


    @Mapping(target = "initiator.id", source = "event.initiator.id")
    @Mapping(target = "initiator.name", source = "event.initiator.name")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    EventFullDto eventToEventFullDto(Event event);

    @BeforeMapping
    default void fillNeedAttributes(NewEventDto dto, @MappingTarget Event event) {
        event.setCreatedOn(LocalDateTime.now());
        event.setState(StateStatus.PENDING);
        event.setViews(0L);
        event.setParticipants(0);
        if (dto.getPaid() == null) {
            event.setPaid(false);
        }

        if (dto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (dto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
    }
}
