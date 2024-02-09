package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable(name = "userId") long userId,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Обработка запроса на получение событий пользователя с id = {} и парамтрами from = {}, size = {}",
                userId, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<EventShortDto> result = eventService.getUserEvents(userId, pageRequest);
        log.info("Получен список событий длиной {}", result.size());
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postNewEvent(@PathVariable(name = "userId") long userId,
                                     @Valid @RequestBody NewEventDto dto) {
        log.info("Обработка запроса на создание нового события пользоветем с id = {}", userId);
        log.info("Тело запроса: {}", dto);
        EventFullDto result = eventService.createNewEvent(userId, dto);
        log.info("Создано событие с id = {}", result.getId());
        return result;
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "userId") long userId,
                                 @PathVariable(name = "eventId") long eventId) {
        log.info("Обработка запроса на получение полной информации о событии с id = {}, созданного пользователем с id = {}", eventId, userId);
        EventFullDto result = eventService.getUserEvent(userId, eventId);
        log.info("Получено событие с id = {}", result.getId());
        return result;
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto patchEvent(@PathVariable(name = "userId") long userId,
                                   @PathVariable(name = "eventId") long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest eventUpdates) {
        log.info("Обработка запроса на обновление события c id = {} пользоватем с id = {}", eventId, userId);
        log.info("Тело запроса: {}", eventUpdates);
        EventFullDto result = eventService.updateEvent(userId, eventId, eventUpdates);
        log.info("Обновлено событие с id = {}", result.getId());
        return result;
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsInfo(@PathVariable(name = "userId") long userId,
                                                         @PathVariable(name = "eventId") long eventId) {
        log.info("Обработка запроса на получение информации о запросах на событие с id = {}, созданное пользователем с id ={}", eventId, userId);
        List<ParticipationRequestDto> result = requestService.getRequestsForEvent(userId, eventId);
        log.info("Получен список длиной {}", result.size());
        return result;
    }

    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResult acceptRequests(@PathVariable(name = "userId") long userId,
                                                         @PathVariable(name = "eventId") long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Обработка запроса на модерацию заявок на событие с id = {}, созданного пользователем c id = {}", eventId, userId);
        EventRequestStatusUpdateResult result = requestService.updateRequestStatusesForEvent(userId, eventId, request);
        log.info("Получен список заявок, оборено: {}, отклонено: {}", result.getConfirmedRequests().size(),
                result.getRejectedRequests().size());
        return result;
    }


}
