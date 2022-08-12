package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId);

    User createUser(User userDtoService);

    User updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

    boolean isUserExists(Long userId);
}
