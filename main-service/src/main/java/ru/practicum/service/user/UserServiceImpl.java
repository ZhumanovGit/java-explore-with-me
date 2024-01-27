package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        List<User> users = repository.findAllByIdIn(ids, pageable);
        return users.stream()
                .map(UserMapper.INSTANCE::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.INSTANCE.newUserRequestToUser(request);
        return UserMapper.INSTANCE.userToUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        repository.deleteById(userId);
    }
}
