package ru.practicum.service.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.EventAdminSearchRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventPublicSearchRequest;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;

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


}
