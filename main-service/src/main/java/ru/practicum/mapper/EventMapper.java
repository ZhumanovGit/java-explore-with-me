package ru.practicum.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.entity.enums.StateStatus;

import java.time.LocalDateTime;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "lat", source = "dto.location.lat")
    @Mapping(target = "lon", source = "dto.location.lon")
    @Mapping(target = "initiator.id", source = "user.id")
    @Mapping(target = "initiator.name", source = "user.name")
    @Mapping(target = "initiator.email", source = "user.email")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", source = "dto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "id", ignore = true)
    Event newEventDtoToEvent(NewEventDto dto, User user);

    @Mapping(target = "initiator.id", source = "initiator.id")
    @Mapping(target = "initiator.name", source = "initiator.name")
    @Mapping(target = "confirmedRequests", source = "participants")
    EventShortDto eventToEventShortDto(Event event);


    @Mapping(target = "initiator.id", source = "event.initiator.id")
    @Mapping(target = "initiator.name", source = "event.initiator.name")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    @Mapping(target = "confirmedRequests", source = "participants")
    EventFullDto eventToEventFullDto(Event event);

    @BeforeMapping
    default void fillNeedAttributes(NewEventDto dto, @MappingTarget Event event) {
        event.setCreatedOn(LocalDateTime.now());
        event.setState(StateStatus.PENDING);
        event.setViews(0L);
        event.setCreatedOn(LocalDateTime.now());
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
