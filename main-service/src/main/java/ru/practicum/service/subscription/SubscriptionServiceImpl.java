package ru.practicum.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.subscription.SubscriptionDto;
import ru.practicum.entity.Subscription;
import ru.practicum.entity.User;
import ru.practicum.entity.enums.SubscribeStatus;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.SubscriptionMapper;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper mapper;

    @Override
    public SubscriptionDto createSubscription(long followerId, long publisherId) {
        if (followerId == publisherId) {
            throw new BadRequestException("User can not subscribe to him self");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followerId + " was not found"));
        User publisher = userRepository.findById(publisherId)
                .orElseThrow(() -> new NotFoundException("User with id=" + publisherId + "was not found"));

        Subscription subscription = Subscription.builder()
                .follower(follower)
                .publisher(publisher)
                .created(LocalDateTime.now())
                .status(SubscribeStatus.ACTIVE)
                .build();
        return mapper.subscriptionToDto(subscriptionRepository.save(subscription));
    }

    @Override
    public SubscriptionDto cancelSubscription(long followerId, long subscriptionId) {
        userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followerId + " was not found"));
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription with id=" + subscriptionId + " was not found"));
        subscription.setStatus(SubscribeStatus.CANCELED);
        return mapper.subscriptionToDto(subscriptionRepository.save(subscription));
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(long followerId) {
        userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followerId + " was not found"));
        List<Subscription> subscriptions = subscriptionRepository
                .findAllByFollowerIdAndStatusIs(followerId, SubscribeStatus.ACTIVE);
        return subscriptions.stream()
                .map(mapper::subscriptionToDto)
                .collect(Collectors.toList());
    }
}
