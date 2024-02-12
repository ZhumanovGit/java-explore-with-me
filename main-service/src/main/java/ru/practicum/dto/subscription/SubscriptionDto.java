package ru.practicum.dto.subscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.enums.SubscribeStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Jacksonized
public class SubscriptionDto {
    private final Long id;
    private final UserShortDto publisher;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created;
    private final SubscribeStatus status;
}
