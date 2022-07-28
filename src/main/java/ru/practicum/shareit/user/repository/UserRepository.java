package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDtoService;

import java.util.List;

public interface UserRepository {
    List<UserDtoService> getAllUsersFromStorage();

    UserDtoService getUserByIdFromStorage(Long userId);

    UserDtoService createUserInStorage(UserDtoService userDtoService);

    UserDtoService updateUserInStorage(Long userId, UserDtoService userDtoService);

    void deleteUserByIdInStorage(Long userId);

    boolean isUserExistsById(Long userId);
}
