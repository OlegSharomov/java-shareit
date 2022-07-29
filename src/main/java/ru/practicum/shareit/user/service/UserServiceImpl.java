package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsersFromStorage();
    }

    @Override
    public User getUserById(Long userId) {
        return repository.getUserByIdFromStorage(userId);
    }

    @Override
    public User createUser(User user) {
        return repository.createUserInStorage(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        return repository.updateUserInStorage(userId, user);
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.deleteUserByIdInStorage(userId);
    }
}
