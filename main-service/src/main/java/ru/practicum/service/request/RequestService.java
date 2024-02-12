package ru.practicum.service.request;

import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestsForEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatusesForEvent(long userId,
                                                                 long eventId,
                                                                 EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto createRequestByUser(long userId, long eventId);

    ParticipationRequestDto cancelRequestByUser(long userId, long requestId);
}
