package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsersFromStorage();

    User getUserByIdFromStorage(Long userId);

    User createUserInStorage(User userDtoService);

    User updateUserInStorage(Long userId, User userDtoService);

    void deleteUserByIdInStorage(Long userId);

    boolean isUserExistsById(Long userId);
}
