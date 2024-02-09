package ru.practicum.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatRequest;
import ru.practicum.dto.event.EventAdminSearchRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicSearchRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSubSearchRequest;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.ParticipantRequest;
import ru.practicum.entity.QEvent;
import ru.practicum.entity.Subscription;
import ru.practicum.entity.User;
import ru.practicum.entity.enums.AdminStateAction;
import ru.practicum.entity.enums.RequestStatus;
import ru.practicum.entity.enums.StateStatus;
import ru.practicum.entity.enums.SubscribeStatus;
import ru.practicum.entity.enums.UserStateAction;
import ru.practicum.exception.model.EventModerationException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final StatClient statClient;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getUserEvents(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        log.info("im working");
        Map<Long, Integer> eventsParticipants = getParticipants(eventIds);
        Map<Long, Long> eventsViews = getViews(eventIds);
        List<Event> filledEvents = events.stream()
                .peek(e -> e.setParticipants(eventsParticipants.getOrDefault(e.getId(), 0)))
                .peek(e -> e.setViews(eventsViews.getOrDefault(e.getViews(), 0L)))
                .collect(Collectors.toList());
        return filledEvents.stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createNewEvent(long userId, NewEventDto dto) {
        LocalDateTime minimalDate = LocalDateTime.parse(dto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (minimalDate.minusHours(2L).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("EventDate must be earlier than 2 hours before now");
        }
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventMapper.newEventDtoToEvent(dto, initiator);
        eventMapper.fillNeedAttributes(dto, event);
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + "was not found"));
        event.setCategory(category);
        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEvent(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        Map<Long, Integer> eventParticipants = getParticipants(List.of(eventId));
        Map<Long, Long> eventViews = getViews(List.of(eventId));
        event.setParticipants(eventParticipants.getOrDefault(eventId, 0));
        event.setViews(eventViews.getOrDefault(eventId, 0L));
        return eventMapper.eventToEventFullDto(event);
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
        if (event.getEventDate().minusHours(2L).isBefore(LocalDateTime.now())) {
            throw new EventModerationException("EventDate must be earlier than 2 hours before now");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        if (event.getInitiator().getId() != userId) {
            throw new EventModerationException("This user was no privileges for this action");
        }

        Event newEvent = event.toBuilder()
                .annotation(request.getAnnotation() != null ? request.getAnnotation() : event.getAnnotation())
                .category(request.getCategory() != null ? categoryRepository.findById(request.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category with id=" + request.getCategory()
                                + " was not found")) : event.getCategory())
                .description(request.getDescription() != null ? request.getDescription() : event.getDescription())
                .lat(request.getLocation() != null ? request.getLocation().getLat() : event.getLat())
                .lon(request.getLocation() != null ? request.getLocation().getLon() : event.getLon())
                .paid(request.getPaid() != null ? request.getPaid() : event.getPaid())
                .participantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : event.getRequestModeration())
                .title(request.getTitle() != null ? request.getTitle() : event.getTitle())
                .build();
        UserStateAction newAction = request.getStateAction();
        if (newAction != null) {
            if (newAction == UserStateAction.SEND_TO_REVIEW) {
                newEvent.setState(StateStatus.PENDING);
            } else {
                newEvent.setState(StateStatus.CANCELED);
            }
        }

        Event updatedEvent = eventRepository.save(newEvent);
        Map<Long, Integer> eventParticipants = getParticipants(List.of(eventId));
        Map<Long, Long> eventViews = getViews(List.of(eventId));
        updatedEvent.setParticipants(eventParticipants.getOrDefault(eventId, 0));
        updatedEvent.setViews(eventViews.getOrDefault(eventId, 0L));
        return eventMapper.eventToEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(EventAdminSearchRequest request, Pageable pageable) {
        LocalDateTime searchRangeStart = request.getRangeStart();
        BooleanExpression expression = QEvent.event.eventDate.after(searchRangeStart != null ? searchRangeStart : LocalDateTime.now());
        List<Long> searchUsers = request.getUsers();
        if (searchUsers != null && !searchUsers.isEmpty()) {
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
        if (searchCategories != null && !searchCategories.isEmpty()) {
            expression.and(QEvent.event.category.id.in(searchCategories));
        }
        LocalDateTime searchRangeEnd = request.getRangeEnd();
        if (searchRangeEnd != null) {
            expression.and(QEvent.event.eventDate.before(searchRangeEnd));
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Integer> eventsParticipants = getParticipants(eventIds);
        Map<Long, Long> eventsViews = getViews(eventIds);
        return events.stream()
                .peek(e -> e.setParticipants(eventsParticipants.getOrDefault(e.getId(), 0)))
                .peek(e -> e.setViews(eventsViews.getOrDefault(e.getId(), 0L)))
                .map(eventMapper::eventToEventFullDto)
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
        if (event.getEventDate().minusHours(1L).isBefore(LocalDateTime.now()) && action == AdminStateAction.PUBLISH_EVENT) {
            throw new EventModerationException("EventDate must be earlier than 1 hour before publication");
        }
        Event newEvent = event.toBuilder()
                .annotation(request.getAnnotation() != null ? request.getAnnotation() : event.getAnnotation())
                .category(request.getCategory() != null ? categoryRepository.findById(request.getCategory())
                        .orElseThrow(() -> new NotFoundException("Category with id=" + request.getCategory()
                                + " was not found")) : event.getCategory())
                .description(request.getDescription() != null ? request.getDescription() : event.getDescription())
                .paid(request.getPaid() != null ? request.getPaid() : event.getPaid())
                .lat(request.getLocation() != null ? request.getLocation().getLat() : event.getLat())
                .lon(request.getLocation() != null ? request.getLocation().getLon() : event.getLon())
                .participantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : event.getRequestModeration())
                .title(request.getTitle() != null ? request.getTitle() : event.getTitle())
                .state(request.getStateAction() == AdminStateAction.PUBLISH_EVENT ? StateStatus.PUBLISHED : StateStatus.CANCELED)
                .publishedOn(request.getStateAction() == AdminStateAction.PUBLISH_EVENT ? LocalDateTime.now() : null)
                .build();
        Event updatedEvent = eventRepository.save(newEvent);
        Map<Long, Integer> eventParticipants = getParticipants(List.of(eventId));
        Map<Long, Long> eventViews = getViews(List.of(eventId));
        updatedEvent.setParticipants(eventParticipants.getOrDefault(eventId, 0));
        updatedEvent.setViews(eventViews.getOrDefault(eventId, 0L));
        return eventMapper.eventToEventFullDto(updatedEvent);
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
        if (searchCategories != null && !searchCategories.isEmpty()) {
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
        if (searchRangeEnd != null && searchRangeStart != null && searchRangeStart.isAfter(searchRangeEnd)) {
            throw new IllegalArgumentException("start moment must be before end moment");
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Integer> eventsParticipants = getParticipants(eventIds);
        if (request.getOnlyAvailable() != null && request.getOnlyAvailable()) {
            return events.stream()
                    .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                    .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                    .filter(event -> event.getParticipantLimit() == 0 || event.getParticipants() < event.getParticipantLimit())
                    .map(eventMapper::eventToEventShortDto)
                    .collect(Collectors.toList());
        }
        return events.stream()
                .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() != StateStatus.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        Map<Long, Long> views = getViews(List.of(eventId));
        Map<Long, Integer> participants = getParticipants(List.of(eventId));
        event.setViews(views.getOrDefault(eventId, 0L));
        event.setParticipants(participants.getOrDefault(eventId, 0));
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getPublisherEvents(EventSubSearchRequest searchRequest, Pageable pageable) {
        long followerId = searchRequest.getFollowerId();
        userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followerId + " was not found"));
        long publisherId = searchRequest.getPublisherId();
        userRepository.findById(publisherId)
                .orElseThrow(() -> new NotFoundException("User with id=" + publisherId + "was not found"));
        BooleanExpression expression = QEvent.event.initiator.id.eq(publisherId);
        if (searchRequest.getOnlyFuture()) {
            expression = expression.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Integer> eventsParticipants = getParticipants(eventIds);
        if (searchRequest.getOnlyAvailable()) {
            return events.stream()
                    .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                    .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                    .filter(event -> event.getParticipantLimit() == 0 || event.getParticipants() < event.getParticipantLimit())
                    .map(eventMapper::eventToEventShortDto)
                    .collect(Collectors.toList());
        }
        return events.stream()
                .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getSubscriptionsEvents(EventSubSearchRequest searchRequest, Pageable pageable) {
        long followerId = searchRequest.getFollowerId();
        userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followerId + " was not found"));
        List<Long> publisherIds = subscriptionRepository
                .findAllByFollowerIdAndStatusIs(followerId, SubscribeStatus.ACTIVE).stream()
                .map(Subscription::getPublisher)
                .map(User::getId)
                .collect(Collectors.toList());
        BooleanExpression expression = QEvent.event.initiator.id.in(publisherIds);
        if (searchRequest.getOnlyFuture()) {
            expression = expression.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
        Page<Event> events = eventRepository.findAll(expression, pageable);
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Integer> eventsParticipants = getParticipants(eventIds);
        if (searchRequest.getOnlyAvailable()) {
            return events.stream()
                    .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                    .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                    .filter(event -> event.getParticipantLimit() == 0 || event.getParticipants() < event.getParticipantLimit())
                    .map(eventMapper::eventToEventShortDto)
                    .collect(Collectors.toList());
        }
        return events.stream()
                .peek(event -> event.setViews(eventsViews.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setParticipants(eventsParticipants.getOrDefault(event.getId(), 0)))
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getViews(final List<Long> eventIds) {
        Pattern eventPathPattern = Pattern.compile("^/events/(?<id>\\d+)(\\?.+)?$");
        LocalDateTime defaultStart = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        List<String> uris = eventIds.stream()
                .map(id -> String.format("/events/%d", id))
                .collect(Collectors.toList());

        List<StatDto> stats = statClient.get(new StatRequest(defaultStart,
                LocalDateTime.now(), uris, true));

        return stats.stream()
                .map(dto -> {
                    Matcher matcher = eventPathPattern.matcher(dto.getUri());
                    if (matcher.matches()) {
                        Long id = Long.valueOf(matcher.group("id"));
                        return new AbstractMap.SimpleEntry<>(id, dto.getHits());
                    }
                    return null;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    private Map<Long, Integer> getParticipants(List<Long> eventIds) {
        List<ParticipantRequest> participants = requestRepository
                .findAllByEventIdInAndStatusIs(eventIds, RequestStatus.CONFIRMED);
        Map<Long, Integer> eventsParticipants = new HashMap<>();
        for (ParticipantRequest participant : participants) {
            Long eventId = participant.getEvent().getId();
            Integer currentParticipants = eventsParticipants.get(eventId);
            if (currentParticipants == null) {
                eventsParticipants.put(eventId, 1);
            } else {
                currentParticipants++;
                eventsParticipants.put(eventId, currentParticipants);
            }
        }
        return eventsParticipants;
    }
}
