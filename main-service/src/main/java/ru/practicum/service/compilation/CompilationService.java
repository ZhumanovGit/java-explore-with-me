package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto dto);
    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    List<CompilationDto> getCompilations(boolean pinned, Pageable pageable);
    CompilationDto getCompilation(long compId);

}
