package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatController {
    private final StatService service;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStat(@Valid @RequestBody CreatingStatDto dto) {
        log.info("Обработка запроса на добавление нового обращения к uri");
        service.createNewStatRecord(dto);
        log.info("Добавлено новое обращение к uri = {}", dto.getUri());
    }

    @GetMapping(path = "/stats")
    public List<StatDto> getStat(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                 @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                 @RequestParam(name = "uris", required = false) Collection<String> uris,
                                 @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {

        log.info("Обработка запроса на получение статистики со следующими параметрами: start = {}, end = {}, unique = {}, uris = {}",
                start, end, unique, uris);
        List<StatDto> result = service.getStats(start, end, uris, unique);
        log.info("Получена статистика, список длинной {}", result.size());
        return result;
    }
}
