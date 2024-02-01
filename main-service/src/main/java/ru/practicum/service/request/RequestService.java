package ru.practicum.service.request;

import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto getUserStatusForEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatusesForEvent(long userId,
                                                                 long eventId,
                                                                 EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto createRequestByUser(long userId, long eventId);

    ParticipationRequestDto cancelRequestByUser(long userId, long requestId);
}
