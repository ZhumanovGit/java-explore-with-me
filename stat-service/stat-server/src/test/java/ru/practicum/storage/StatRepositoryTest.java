package ru.practicum.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StatRepository repository;

    @Test
    public void countHitsByAppAndUriForPeriod_whenDbIsEmpty_thenReturnEmptyList() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);

        List<ViewStat> stats = repository.countHits(start, end, null);

        assertEquals(0, stats.size());
    }

    @Test
    public void countHitsByAppAndUriForPeriod_whenDbIsFilled_thenReturnListOfViewStats() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        Stat first = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(2L))
                .build());
        Stat second = entityManager.merge(Stat.builder()
                .app("test2")
                .uri("test2")
                .ip("test2")
                .timestamp(start.plusDays(3L))
                .build());
        Stat third = entityManager.merge(Stat.builder()
                .app("test3")
                .uri("test3")
                .ip("test3")
                .timestamp(start.plusDays(4L))
                .build());

        List<ViewStat> stats = repository.countHits(start, end, null);

        assertEquals(3, stats.size());
    }

    @Test
    public void countHitsByAppAndUriForPeriod_whenDbIsFilledWithUriParam_thenReturnListOfViewStats() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        Stat first = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(2L))
                .build());
        Stat second = entityManager.merge(Stat.builder()
                .app("test2")
                .uri("test2")
                .ip("test2")
                .timestamp(start.plusDays(3L))
                .build());
        Stat third = entityManager.merge(Stat.builder()
                .app("test3")
                .uri("test3")
                .ip("test3")
                .timestamp(start.plusDays(4L))
                .build());

        List<ViewStat> stats = repository.countHits(start, end, List.of("test", "test3"));

        assertEquals(2, stats.size());
    }

    @Test
    public void countUniqueHitsByAppAndUriForPeriod_whenDbIsEmpty_whenReturnEmptyList() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);

        List<ViewStat> stats = repository.countUniqueHits(start, end, null);

        assertEquals(0, stats.size());
    }

    @Test
    public void countUniqueHitsByAppAndUriForPeriod_whenDbIsFilled_whenReturnListOfViewStats() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        Stat first = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(2L))
                .build());
        Stat firstAgain = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(5L))
                .build());
        Stat second = entityManager.merge(Stat.builder()
                .app("test2")
                .uri("test2")
                .ip("test2")
                .timestamp(start.plusDays(3L))
                .build());
        Stat third = entityManager.merge(Stat.builder()
                .app("test3")
                .uri("test3")
                .ip("test3")
                .timestamp(start.plusDays(4L))
                .build());

        List<ViewStat> stats = repository.countUniqueHits(start, end, null);

        assertEquals(3, stats.size());
    }

    @Test
    public void countUniqueHitsByAppAndUriForPeriod_whenDbIsFilledWithUriParam_whenReturnListOfViewStats() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        Stat first = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(2L))
                .build());
        Stat firstAgain = entityManager.merge(Stat.builder()
                .app("test")
                .uri("test")
                .ip("test")
                .timestamp(start.plusDays(5L))
                .build());
        Stat second = entityManager.merge(Stat.builder()
                .app("test2")
                .uri("test2")
                .ip("test2")
                .timestamp(start.plusDays(3L))
                .build());
        Stat third = entityManager.merge(Stat.builder()
                .app("test3")
                .uri("test3")
                .ip("test3")
                .timestamp(start.plusDays(4L))
                .build());

        List<ViewStat> stats = repository.countUniqueHits(start, end, List.of("test", "test3"));

        assertEquals(2, stats.size());
    }

}