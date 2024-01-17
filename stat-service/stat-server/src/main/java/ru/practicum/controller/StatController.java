package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatController {
    private final StatService service;

    @PostMapping(path = "/hit")
    public void saveStat(@Valid @RequestBody CreatingStatDto dto) {
        log.info("Обработка запроса на добавление нового обращения к uri");
        service.createNewStatRecord(dto);
        log.info("Добавлено новое обращение к uri = {}", dto.getUri());
    }

    @GetMapping(path = "/stats")
    public List<StatDto> getStat(@RequestParam(name = "start") String start,
                                 @RequestParam(name = "end") String end,
                                 @RequestParam(name = "uris", required = false) List<String> uris,
                                 @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {

        log.info("Обработка запроса на получение статистики со следующими параметрами: start = {}, end = {}, unique = {}",
                start, end, unique);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<StatDto> result = service.getStats(startTime, endTime, uris, unique);
        log.info("Получена статистика, список длинной {}", result.size());
        return result;
    }
}
