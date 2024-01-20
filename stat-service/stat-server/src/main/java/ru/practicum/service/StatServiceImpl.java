package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatMapper;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;
import ru.practicum.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private final StatMapper mapper;

    @Override
    @Transactional
    public void createNewStatRecord(CreatingStatDto dto) {
        Stat stat = mapper.creatingDtoToStat(dto);
        repository.save(stat);
    }

    @Override
    @Transactional
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStat> stats;
        if (unique) {
            stats = repository.countUniqueHitsByAppAndUriForPeriod(start, end, uris);
        } else {
            stats = repository.countHitsByAppAndUriForPeriod(start, end, uris);
        }
        return stats.stream()
                .map(mapper::viewStatToStatDto)
                .collect(Collectors.toList());
    }
}
