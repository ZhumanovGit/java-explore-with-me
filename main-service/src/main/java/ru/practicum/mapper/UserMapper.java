package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.User;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    UserShortDto userToUserShortDto(User user);

    User newUserRequestToUser(NewUserRequest request);
}
