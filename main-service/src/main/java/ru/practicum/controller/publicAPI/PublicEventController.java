package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventPublicSearchRequest;
import ru.practicum.dto.EventShortDto;
import ru.practicum.entity.EventSort;
import ru.practicum.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> searchEvents(@RequestParam(name = "text", required = false) String text,
                                            @RequestParam(name = "categories", required = false) List<Long> categories,
                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                            @RequestParam(name = "rangeStart", required = false) LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                            @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(name = "sort", required = false, defaultValue = "EVENT_DATE") String sort,
                                            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                            HttpServletRequest request) {
        log.info("Обработка запроса на получение событий с параметрами text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        log.info("uri: {}", request.getRequestURI());
        EventSort sortParam = EventSort.from(sort)
                .orElseThrow(() -> new IllegalArgumentException("Wrong sort param. Value: " + sort));
        Sort needSort;
        if (sortParam == EventSort.EVENT_DATE) {
            needSort = Sort.by(Sort.Direction.DESC, "eventDate");
        } else {
            needSort = Sort.by(Sort.Direction.DESC, "views");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, needSort);
        EventPublicSearchRequest searchParams = new EventPublicSearchRequest(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        List<EventShortDto> result = service.getEvents(searchParams, pageRequest);
        log.info("Получен список длиной {}", result.size());
        return result;
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "eventId") long eventId,
                                 HttpServletRequest request) {
        log.info("uri: {}", request.getRequestURI());
        log.info("Обработка запроса на получение события с id = {}", eventId);
        EventFullDto result = service.getEventById(eventId);
        log.info("Получено событие с id = {}", result.getId());
        return result;
    }

}
