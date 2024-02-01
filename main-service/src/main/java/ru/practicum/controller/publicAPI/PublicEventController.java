package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    // это надо сохранить в стату
    @GetMapping
    public List<EventShortDto> searchEvents(@RequestParam(name = "text", required = false) String text,
                                            @RequestParam(name = "categories", required = false) List<Long> categories,
                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                            @RequestParam(name = "rangeStart", required = false) LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                            @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(name = "sort", required = false) String sort,
                                            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                            HttpServletRequest request) {
        return null;
    }

    // это надо схоранить в стату
    @GetMapping(path = "/{eventId}")
    public EventShortDto getEvent(@PathVariable(name = "eventId") long eventId,
                                  HttpServletRequest request) {
        return null;
    }

}
