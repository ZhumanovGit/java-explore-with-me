package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
