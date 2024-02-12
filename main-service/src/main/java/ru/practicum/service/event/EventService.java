package ru.practicum.service.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventAdminSearchRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicSearchRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSubSearchRequest;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(long userId, Pageable pageable);

    EventFullDto createNewEvent(long userId, NewEventDto dto);

    EventFullDto getUserEvent(long userId, long eventId);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest request);

    List<EventFullDto> searchEventsByAdmin(EventAdminSearchRequest request, Pageable pageable);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getEvents(EventPublicSearchRequest request, Pageable pageable);

    EventFullDto getEventById(long eventId);

    List<EventShortDto> getPublisherEvents(EventSubSearchRequest searchRequest, Pageable pageable);

    List<EventShortDto> getSubscriptionsEvents(EventSubSearchRequest searchRequest, Pageable pageable);
}
