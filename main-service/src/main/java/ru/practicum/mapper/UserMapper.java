package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.entity.User;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    UserShortDto userToUserShortDto(User user);

    User newUserRequestToUser(NewUserRequest request);
}
