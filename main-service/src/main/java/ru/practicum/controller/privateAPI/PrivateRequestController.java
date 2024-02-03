package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsInfo(@PathVariable(name = "userId") long userId) {
        log.info("Обработка запроса на получение заявок пользователя с id = {}", userId);
        List<ParticipationRequestDto> result = requestService.getUserRequests(userId);
        log.info("Получен список заявок длиной {}", result.size());
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(@PathVariable(name = "userId") long userId,
                                               @RequestParam(name = "eventId") long eventId) {
        log.info("Обработка запроса на создание новой заявки польователем с id = {} на событие с id = {}", userId, eventId);
        ParticipationRequestDto result = requestService.createRequestByUser(userId, eventId);
        log.info("Создана заявка с id = {}", result.getId());
        return result;
    }

    @PatchMapping(path = "/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") long userId,
                                                 @PathVariable(name = "requestId") long requestId) {
        log.info("Обработка запроса на отмену заявки c id = {} пользователя с id = {}", requestId, userId);
        ParticipationRequestDto result = requestService.cancelRequestByUser(userId, requestId);
        log.info("Заявка с id = {} отменена", result.getId());
        return result;
    }
}
