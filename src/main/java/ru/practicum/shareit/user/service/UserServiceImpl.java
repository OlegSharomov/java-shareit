package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDtoService> getAllUsers() {
        return repository.getAllUsersFromStorage();
    }

    @Override
    public UserDtoService getUserById(Long userId) {
        return repository.getUserByIdFromStorage(userId);
    }

    @Override
    public UserDtoService createUser(UserDtoService userDtoService) {
        return repository.createUserInStorage(userDtoService);
    }

    @Override
    public UserDtoService updateUser(Long userId, UserDtoService userDtoService) {
        return repository.updateUserInStorage(userId, userDtoService);
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.deleteUserByIdInStorage(userId);
    }
}
