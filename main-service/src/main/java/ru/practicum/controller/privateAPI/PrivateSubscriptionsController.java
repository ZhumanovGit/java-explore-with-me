package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSubSearchRequest;
import ru.practicum.dto.subscription.SubscriptionDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.subscription.SubscriptionService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class PrivateSubscriptionsController {
    private final SubscriptionService subscriptionService;
    private final EventService eventService;


    @PostMapping
    public SubscriptionDto subscribeToUser(@PathVariable(name = "userId") long userId,
                                           @RequestParam(name = "publisherId") long publisherId) {
        log.info("Обработка запроса на создание новой подписки пользователя с id = {} на пользователся с id = {}", userId, publisherId);
        SubscriptionDto result = subscriptionService.createSubscription(userId, publisherId);
        log.info("Создана новая подписка с id = {}", result.getId());
        return result;
    }

    @PatchMapping(path = "/{subscriptionId}/cancel")
    public SubscriptionDto cancelSubscription(@PathVariable(name = "userId") long userId,
                                              @PathVariable(name = "subscriptionId") long subscriptionId) {
        log.info("Обработка запроса на отмену подписки c id = {} пользователем с id = {}", subscriptionId, userId);
        SubscriptionDto result = subscriptionService.cancelSubscription(userId, subscriptionId);
        log.info("Подписка с id = {} отменена, статус подписки {}", result.getId(), result.getStatus());
        return result;
    }

    @GetMapping
    public List<SubscriptionDto> getSubscriptions(@PathVariable(name = "userId") long userId) {
        log.info("Обработка запроса на получение подписок пользователя с id = {}", userId);
        List<SubscriptionDto> result = subscriptionService.getSubscriptions(userId);
        log.info("Получен список подписок длиной {}", result.size());
        return result;
    }

    @GetMapping(path = "/{publisherId}/events")
    public List<EventShortDto> getPublisherEvents(@PathVariable(name = "userId") long userId,
                                                  @PathVariable(name = "publisherId") long publisherId,
                                                  @RequestParam(name = "onlyFuture", required = false, defaultValue = "true") boolean onlyFuture,
                                                  @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable,
                                                  @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(name = "size", required = false, defaultValue = "10") @Positive int size) {
        log.info("Обработка запроса на получение событий пользователя с id = {} из подписок пользователя с id = {} c параметрами: " +
                "onlyFuture = {}, onlyAvailable = {}, from = {}, size = {}", publisherId, userId, onlyFuture, onlyAvailable, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        EventSubSearchRequest request = EventSubSearchRequest.builder()
                .followerId(userId)
                .publisherId(publisherId)
                .onlyFuture(onlyFuture)
                .onlyAvailable(onlyAvailable)
                .build();
        List<EventShortDto> result = eventService.getPublisherEvents(request, pageRequest);
        log.info("Получен список длиной {}", result.size());
        return result;
    }

    @GetMapping(path = "/events")
    public List<EventShortDto> getSubscriptionsEvents(@PathVariable(name = "userId") long userId,
                                                      @RequestParam(name = "onlyFuture", required = false, defaultValue = "true") boolean onlyFuture,
                                                      @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable,
                                                      @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") @Positive int size) {
        log.info("Обработка запроса на получение событий из подписок пользователя с id = {} c параметрами: " +
                "onlyFuture = {}, onlyAvailable = {}, from = {}, size = {}", userId, onlyFuture, onlyAvailable, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        EventSubSearchRequest request = EventSubSearchRequest.builder()
                .followerId(userId)
                .onlyFuture(onlyFuture)
                .onlyAvailable(onlyAvailable)
                .build();
        List<EventShortDto> result = eventService.getSubscriptionsEvents(request, pageRequest);
        log.info("Получен список длиной {}", result.size());
        return result;
    }
}
