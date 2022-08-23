package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDtoAnswer> getAllUsers();

    // получение пользователя для контроллера
    UserDtoAnswer getUserById(Long userId);

    // получение сущности БД из хранилища
    User getEntityUserByIdFromStorage(Long userId);

    UserDtoAnswer createUser(UserDto userDto);

    UserDtoAnswer updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

    boolean isUserExists(Long userId);
}
