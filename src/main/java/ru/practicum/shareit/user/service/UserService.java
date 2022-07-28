package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDtoService;

import java.util.List;

public interface UserService {
    List<UserDtoService> getAllUsers();

    UserDtoService getUserById(Long userId);

    UserDtoService createUser(UserDtoService userDtoService);

    UserDtoService updateUser(Long userId, UserDtoService userDtoService);

    void deleteUserById(Long userId);
}
