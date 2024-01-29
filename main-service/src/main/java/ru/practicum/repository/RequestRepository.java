package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.ParticipantRequest;

@Repository
public interface RequestRepository extends JpaRepository<ParticipantRequest, Long> {
}