package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.entity.User;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    UserShortDto userToUserShortDto(User user);

    User newUserRequestToUser(NewUserRequest request);
}
