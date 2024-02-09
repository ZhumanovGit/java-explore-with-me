package ru.practicum.service.subscription;

import ru.practicum.dto.subscription.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto createSubscription(long followerId, long publisherId);

    SubscriptionDto cancelSubscription(long followerId, long subscriptionId);

    List<SubscriptionDto> getSubscriptions(long followerId);
}
