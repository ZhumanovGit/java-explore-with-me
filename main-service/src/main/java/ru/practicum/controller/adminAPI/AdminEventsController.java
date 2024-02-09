package ru.practicum.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventAdminSearchRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventsController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                           @RequestParam(name = "states", required = false) List<String> states,
                                           @RequestParam(name = "categories", required = false) List<Long> categories,
                                           @RequestParam(name = "rangeStart", required = false) LocalDateTime rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Обработка запроса на поиск ивентов для админа с параметрами users = {}, states = {}, categories = {}, " +
                        "rangeStart = {}, rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd,
                from, size);
        EventAdminSearchRequest request = new EventAdminSearchRequest(users, states, categories, rangeStart, rangeEnd);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        List<EventFullDto> result = service.searchEventsByAdmin(request, pageRequest);
        log.info("Получен список длиной {}", result.size());
        return result;
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto acceptEvent(@PathVariable(name = "eventId") long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest request) {
        log.info("Обработка запроса на обновление и публикацию события с id = {} админом", eventId);
        EventFullDto result = service.updateEventByAdmin(eventId, request);
        log.info("Событие с id = {} получило статус {}", result.getId(), result.getState());
        return result;
    }
}
