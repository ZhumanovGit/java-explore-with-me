package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {


    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable(name = "userId") long userId,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return null;
    }

    @PostMapping
    public EventFullDto postNewEvent(@PathVariable(name = "userId") long userId,
                                     @Valid @RequestBody NewEventDto dto) {
        return null;
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "userId") long userId,
                                 @PathVariable(name = "eventId") long eventId) {
        return null;
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto patchEvent(@PathVariable(name = "userId") long userId,
                                   @PathVariable(name = "eventId") long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest eventUpdates) {
        return null;
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsInfo(@PathVariable(name = "userId") long userId,
                                                         @PathVariable(name = "eventId") long eventId) {
        return null;
    }

    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResult acceptRequests(@PathVariable(name = "userId") long userId,
                                                         @PathVariable(name = "eventId") long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest request) {
        return null;
    }


}
