package ru.practicum.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Обработка запроса на создание новой подборки");
        CompilationDto result = service.createCompilation(dto);
        log.info("Создана новая подборка с id = {}", result.getId());
        return result;
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") long compId) {
        log.info("Обработка запроса на удаление подборки c id = {}", compId);
        service.deleteCompilation(compId);
        log.info("Подборка с id = {} удалена", compId);
    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto patchCompilation(@PathVariable(name = "compId") long compId,
                                           @Valid @RequestBody UpdateCompilationRequest request) {
        log.info("Обработка запроса на обновление подборки с id = {}", compId);
        CompilationDto result = service.updateCompilation(compId, request);
        log.info("Подборка с id = {} обновлена", result.getId());
        return result;
    }
}
