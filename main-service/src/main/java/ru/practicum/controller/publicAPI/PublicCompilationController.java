package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Обработка запроса на получение подборок с параметрами pinned = {}, from = {}, size = {}", pinned, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<CompilationDto> result = service.getCompilations(pinned, pageRequest);
        log.info("Получен список подборок длиной {}", result.size());
        return result;
    }

    @GetMapping(path = "/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Обработка запроса на получение подборки с id = {}", compId);
        CompilationDto result = service.getCompilation(compId);
        log.info("Получена подборка из {} событий", result.getEvents().size());
        return result;
    }
}
