package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.subscription.SubscriptionDto;
import ru.practicum.entity.Subscription;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "publisher.id", source = "publisher.id")
    @Mapping(target = "publisher.name", source = "publisher.name")
    SubscriptionDto subscriptionToDto(Subscription sub);
}
