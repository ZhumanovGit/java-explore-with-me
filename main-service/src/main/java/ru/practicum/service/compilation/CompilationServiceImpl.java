package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(dto);
        compilationMapper.fillNeedAttributes(dto, compilation);
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + compId + " was not found"));
        compilationRepository.deleteById(compId);


    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + compId + " was not found"));
        Compilation newCompilation = compilation.toBuilder()
                .pinned(request.getPinned() != null ? request.getPinned() : compilation.getPinned())
                .title(request.getTitle() != null ? request.getTitle() : compilation.getTitle())
                .build();

        List<Long> eventIds = request.getEvents();
        if (eventIds == null) {
            return compilationMapper.compilationToCompilationDto(compilationRepository.save(newCompilation));
        }
        List<Event> newEvents = eventRepository.findAllByIdIn(eventIds);
        newCompilation.setEvents(new HashSet<>(newEvents));

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, Pageable pageable) {
        List<Compilation> compilations;
        if (pinned) {
            compilations = compilationRepository.findAllByPinned(true, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }
        return compilations.stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationMapper.compilationToCompilationDto(compilation);
    }
}
