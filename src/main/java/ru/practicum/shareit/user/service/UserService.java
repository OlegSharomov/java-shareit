package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId);

    User createUser(User userDtoService);

    User updateUser(Long userId, User userDtoService);

    void deleteUserById(Long userId);
}
