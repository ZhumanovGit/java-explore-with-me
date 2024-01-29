package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = CompilationMapper.INSTANCE.newCompilationDtoToCompilation(dto);
        Compilation newCompilation = compilationRepository.save(compilation);
        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
        if (!events.isEmpty()) {
            List<Event> newEvents = events.stream()
                    .peek(event -> event.getEventCompilations().add(newCompilation))
                    .collect(Collectors.toList());
            eventRepository.saveAll(newEvents);
        }
        return CompilationMapper.INSTANCE.compilationToCompilationDto(newCompilation);
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
        Boolean pinned = request.getPinned();
        if (pinned != null) {
            compilation.setPinned(pinned);
        }
        String title = request.getTitle();
        if (title != null) {
            compilation.setTitle(title);
        }
        List<Long> eventIds = request.getEvents();
        if (eventIds.isEmpty()) {
            return CompilationMapper.INSTANCE.compilationToCompilationDto(compilationRepository.save(compilation));
        }
        List<Event> events = eventRepository.findAllByEventCompilationsId(compId);
        if (!events.isEmpty()) {
            List<Event> oldEvents = events.stream()
                    .peek(event -> event.getEventCompilations().remove(compilation))
                    .collect(Collectors.toList());
            eventRepository.saveAll(oldEvents);
        }

        List<Event> newEvents = eventRepository.findAllByIdIn(eventIds).stream()
                .peek(event -> event.getEventCompilations().add(compilation))
                .collect(Collectors.toList());
        eventRepository.saveAll(newEvents);

        return CompilationMapper.INSTANCE.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, Pageable pageable) {
        List<Compilation> compilations;
        if (pinned) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }
        return compilations.stream()
                .map(CompilationMapper.INSTANCE::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.INSTANCE.compilationToCompilationDto(compilation);
    }
}
