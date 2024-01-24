package ru.practicum.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {


    @PostMapping
    public CompilationDto postCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return null;
    }

    @DeleteMapping(path = "/{compId}")
    public void deleteCompilation(@PathVariable(name = "compId") long compId) {

    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto patchCompilation(@PathVariable(name = "compId") long compId,
                                           @Valid @RequestBody UpdateCompilationRequest request) {
        return null;
    }
}
