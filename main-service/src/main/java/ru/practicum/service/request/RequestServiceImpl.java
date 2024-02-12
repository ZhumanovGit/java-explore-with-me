package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.ParticipantRequest;
import ru.practicum.entity.User;
import ru.practicum.entity.enums.RequestStatus;
import ru.practicum.entity.enums.StateStatus;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.RequestModerationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsForEvent(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " was not found"));

        List<ParticipantRequest> requests = requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId);

        return requests.stream()
                .map(requestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatusesForEvent(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " was not found"));
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Event with id=" + eventId + " has no initiator with id=" + userId);
        }

        Integer currentParticipants = getParticipantsForEvent(eventId);
        if (currentParticipants >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new RequestModerationException("For event with id=" + eventId + " has no available places");
        }

        List<ParticipantRequest> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            List<ParticipantRequest> updatedRequests = requests.stream()
                    .peek(item -> {
                        if (item.getStatus() != RequestStatus.PENDING) {
                            throw new RequestModerationException("Request with id=" + item.getId() + " has wrong status");
                        }
                        item.setStatus(RequestStatus.CONFIRMED);
                    })

                    .collect(Collectors.toList());
            requestRepository.saveAll(updatedRequests);
            List<ParticipationRequestDto> result = updatedRequests.stream()
                    .map(requestMapper::requestToRequestDto)
                    .collect(Collectors.toList());
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(result)
                    .build();

        }
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        if (request.getStatus() == RequestStatus.REJECTED) {
            for (ParticipantRequest item : requests) {
                item.setStatus(RequestStatus.REJECTED);
                rejected.add(requestMapper.requestToRequestDto(item));
            }
            return new EventRequestStatusUpdateResult(confirmed, rejected);
        }
        int enablePlaces = event.getParticipantLimit() - currentParticipants;
        if (enablePlaces >= requests.size()) {
            for (ParticipantRequest item : requests) {
                if (item.getStatus() != RequestStatus.PENDING) {
                    throw new RequestModerationException("Request with id=" + item.getId() + " has wrong status");
                }
                item.setStatus(RequestStatus.CONFIRMED);
                confirmed.add(requestMapper.requestToRequestDto(item));
            }
            return new EventRequestStatusUpdateResult(confirmed, rejected);
        }

        for (int i = 0; i <= enablePlaces; i++) {
            ParticipantRequest item = requests.get(i);
            if (item.getStatus() != RequestStatus.PENDING) {
                throw new RequestModerationException("Request with id=" + item.getId() + " has wrong status");
            }
            item.setStatus(RequestStatus.CONFIRMED);
            confirmed.add(requestMapper.requestToRequestDto(item));
        }
        for (int i = enablePlaces + 1; i < requests.size(); i++) {
            ParticipantRequest item = requests.get(i);
            if (item.getStatus() != RequestStatus.PENDING) {
                throw new RequestModerationException("Request with id=" + item.getId() + " has wrong status");
            }
            item.setStatus(RequestStatus.REJECTED);
            rejected.add(requestMapper.requestToRequestDto(item));
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);


    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<ParticipantRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(requestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequestByUser(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " was not found"));
        if (event.getInitiator().getId() == userId) {
            throw new RequestModerationException("User with id=" + userId + " is initiator of event with id=" + eventId);
        }

        if (event.getState() != StateStatus.PUBLISHED) {
            throw new RequestModerationException("Event with id=" + eventId + " is not published");
        }

        if (getParticipantsForEvent(eventId) >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new RequestModerationException("For event with id=" + eventId + " has no available places");
        }
        LocalDateTime now = LocalDateTime.now();
        if (event.getEventDate().isBefore(now)) {
            throw new RequestModerationException("Event with id=" + eventId + " is already passed");
        }

        Optional<ParticipantRequest> alreadyCreatedRequest = requestRepository
                .findFirstByRequesterIdAndEventId(userId, eventId);
        if (alreadyCreatedRequest.isPresent()) {
            throw new RequestModerationException("Request with requesterId=" + userId + " and eventId=" + eventId +
                    " already exists");
        }

        ParticipantRequest request = ParticipantRequest.builder()
                .created(now)
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipantRequest createdRequest = requestRepository.save(request);

        return requestMapper.requestToRequestDto(createdRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByUser(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        ParticipantRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);
        return requestMapper.requestToRequestDto(request);
    }

    private Integer getParticipantsForEvent(long eventId) {
        List<ParticipantRequest> participants = requestRepository.findAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);
        return participants.size();
    }
}
