package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByIdIn(List<Long> ids);

    List<Event> findAllByEventCompilationsId(long compId);

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    List<Event> findByCategoryId(long categoryId);
}
