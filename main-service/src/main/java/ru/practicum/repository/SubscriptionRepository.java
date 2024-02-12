package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Subscription;
import ru.practicum.entity.enums.SubscribeStatus;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByFollowerIdAndStatusIs(long followerId, SubscribeStatus status);
}
