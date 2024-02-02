package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.ParticipantRequest;
import ru.practicum.entity.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipantRequest, Long> {

    List<ParticipantRequest> findAllByRequesterId(long requesterId);

    Optional<ParticipantRequest> findFirstByRequesterIdAndEventId(long requesterId, long eventId);

    List<ParticipantRequest> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    List<ParticipantRequest> findAllByIdIn(List<Long> ids);
}
