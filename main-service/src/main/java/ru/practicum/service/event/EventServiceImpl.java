package ru.practicum.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventAdminSearchRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventPublicSearchRequest;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.entity.AdminStateAction;
import ru.practicum.entity.Event;
import ru.practicum.entity.QEvent;
import ru.practicum.entity.StateStatus;
import ru.practicum.entity.User;
import ru.practicum.entity.UserStateAction;
import ru.practicum.exception.model.EventModerationException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getUserEvents(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper.INSTANCE::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createNewEvent(long userId, NewEventDto dto) {
        LocalDateTime minimalDate = dto.getEventDate().plusHours(2);
        if (minimalDate.isAfter(LocalDateTime.now())) {
            throw new EventModerationException("EventDate must be earlier than 2 hours before now");
        }
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = EventMapper.INSTANCE.newEventDtoToEvent(dto, initiator);
        return EventMapper.INSTANCE.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEvent(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.INSTANCE.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() == StateStatus.PUBLISHED) {
            throw new EventModerationException("Event must not be modified after publication");
        }
        LocalDateTime eventDate = request.getEventDate();
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (event.getEventDate().plusHours(2).isAfter(LocalDateTime.now())) {
            throw new EventModerationException("EventDate must be earlier than 2 hours before now");
        }
        Event newEvent = event.toBuilder()
                .annotation(request.getAnnotation() != null ? request.getAnnotation() : event.getAnnotation())
                .category(request.getCategory() != null ? categoryRepository.findById(request.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category with id=" + request.getCategory()
                                + " was not found")) : event.getCategory())
                .description(request.getDescription() != null ? request.getDescription() : event.getDescription())
                .lat(request.getLocation().getLat() != null ? request.getLocation().getLat() : event.getLat())
                .lon(request.getLocation().getLon() != null ? request.getLocation().getLon() : event.getLon())
                .paid(request.getPaid() != null ? request.getPaid() : event.getPaid())
                .participantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : event.getRequestModeration())
                .title(request.getTitle() != null ? request.getTitle() : event.getTitle())
                .build();
        UserStateAction newAction = request.getStateAction();
        if (newAction != null) {
            if (newAction == UserStateAction.SEND_REVIEW) {
                newEvent.setState(StateStatus.PENDING);
            } else {
                newEvent.setState(StateStatus.CANCELED);
            }
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        if (newEvent.getInitiator().getId() != userId) {
            throw new EventModerationException("This user was no privileges for this action");
        }
        Event updatedEvent = eventRepository.save(newEvent);
        return EventMapper.INSTANCE.eventToEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(EventAdminSearchRequest request, Pageable pageable) {
        LocalDateTime searchRangeStart = request.getRangeStart();
        BooleanExpression expression = QEvent.event.eventDate.after(searchRangeStart != null ? searchRangeStart : LocalDateTime.now());
        List<Long> searchUsers = request.getUsers();
        if (!searchUsers.isEmpty()) {
            expression.and(QEvent.event.initiator.id.in(searchUsers));
        }
        List<StateStatus> searchStatuses = new ArrayList<>();
        List<String> states = request.getStates();
        if (states != null) {
            for (String state : request.getStates()) {
                searchStatuses.add(StateStatus.from(state)
                        .orElseThrow(() -> new IllegalArgumentException("State with name = " + state + " was not found")));
            }
        }
        if (!searchStatuses.isEmpty()) {
            expression.and(QEvent.event.state.in(searchStatuses));
        }
        List<Long> searchCategories = request.getCategories();
        if (!searchCategories.isEmpty()) {
            expression.and(QEvent.event.category.id.in(searchCategories));
        }
        LocalDateTime searchRangeEnd = request.getRangeEnd();
        if (searchRangeEnd != null) {
            expression.and(QEvent.event.eventDate.before(searchRangeEnd));
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        return events.stream()
                .map(EventMapper.INSTANCE::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        StateStatus currentStatus = event.getState();
        AdminStateAction action = request.getStateAction();
        if (currentStatus == StateStatus.PUBLISHED && action == AdminStateAction.REJECT_EVENT) {
            throw new EventModerationException("Event must not be rejected after publication");
        }
        if (currentStatus != StateStatus.PENDING && action == AdminStateAction.PUBLISH_EVENT) {
            throw new EventModerationException("Event is not ready for publication");
        }
        LocalDateTime eventDate = request.getEventDate();
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (event.getEventDate().plusHours(1).isAfter(LocalDateTime.now()) && action == AdminStateAction.PUBLISH_EVENT) {
            throw new EventModerationException("EventDate must be earlier than 1 hour before publication");
        }
        Event newEvent = event.toBuilder()
                .annotation(request.getAnnotation() != null ? request.getAnnotation() : event.getAnnotation())
                .category(request.getCategory() != null ? categoryRepository.findById(request.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category with id=" + request.getCategory()
                                + " was not found")) : event.getCategory())
                .description(request.getDescription() != null ? request.getDescription() : event.getDescription())
                .lat(request.getLocation().getLat() != null ? request.getLocation().getLat() : event.getLat())
                .lon(request.getLocation().getLon() != null ? request.getLocation().getLon() : event.getLon())
                .paid(request.getPaid() != null ? request.getPaid() : event.getPaid())
                .participantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : event.getRequestModeration())
                .title(request.getTitle() != null ? request.getTitle() : event.getTitle())
                .build();
        Event updatedEvent = eventRepository.save(newEvent);
        return EventMapper.INSTANCE.eventToEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getEvents(EventPublicSearchRequest request, Pageable pageable) {
        BooleanExpression expression = QEvent.event.state.eq(StateStatus.PUBLISHED);
        String searchText = request.getText();
        if (searchText != null && !searchText.isBlank()) {
            expression.and(QEvent.event.annotation.containsIgnoreCase(searchText)
                    .or(QEvent.event.description.containsIgnoreCase(searchText)));
        }
        List<Long> searchCategories = request.getCategories();
        if (!searchCategories.isEmpty()) {
            expression.and(QEvent.event.category.id.in(searchCategories));
        }
        Boolean searchPaid = request.getPaid();
        if (searchPaid != null) {
            expression.and(QEvent.event.paid.eq(searchPaid));
        }
        LocalDateTime searchRangeStart = request.getRangeStart();
        expression.and(QEvent.event.eventDate.after(searchRangeStart != null ? searchRangeStart : LocalDateTime.now()));
        LocalDateTime searchRangeEnd = request.getRangeEnd();
        if (searchRangeEnd != null) {
            expression.and(QEvent.event.eventDate.before(searchRangeEnd));
        }
        if (request.getOnlyAvailable()) {
            expression.and(QEvent.event.participants.lt(QEvent.event.participantLimit)
                    .or(QEvent.event.participantLimit.eq(0)));
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        return events.stream()
                .map(EventMapper.INSTANCE::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.INSTANCE.eventToEventFullDto(event);
    }
}
