package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;
import ru.practicum.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private final StatMapper mapper;

    @Override
    @Transactional
    public void createNewStatRecord(CreatingStatDto dto) {
        Stat stat = mapper.dtoToStat(dto);
        repository.save(stat);
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {
        if (start.equals(end) || start.isAfter(end)) {
            throw new BadRequestException("Start can be before end");
        }
        List<ViewStat> stats;
        if (unique) {
            stats = repository.countUniqueHits(start, end, uris);
        } else {
            stats = repository.countHits(start, end, uris);
        }
        return mapper.listViewStatToListDto(stats);
    }
}
