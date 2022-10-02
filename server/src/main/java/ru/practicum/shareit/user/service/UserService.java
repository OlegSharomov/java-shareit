package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDtoAnswer> getAllUsers();

    // getting a user for the controller
    UserDtoAnswer getUserById(Long userId);

    // getting the user's DB entity from storage
    User getEntityUserByIdFromStorage(Long userId);

    UserDtoAnswer createUser(UserDto userDto);

    UserDtoAnswer updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

    boolean isUserExists(Long userId);
}
