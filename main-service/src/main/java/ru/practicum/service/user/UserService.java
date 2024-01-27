package ru.practicum.service.user;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.entity.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto createUser(NewUserRequest request);

    void deleteUser(long userId);
}
