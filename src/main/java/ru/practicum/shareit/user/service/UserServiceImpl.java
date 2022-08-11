package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public List<User> getAllUsers() {
//        return repository.getAllUsersFromStorage();
    return repository.findAll();
    }

    @Override
    @Transactional
    public User getUserById(Long userId) {
//        return repository.getUserByIdFromStorage(userId);
    return repository.getReferenceById(userId);
    }

    @Override
    @Transactional(readOnly = false)
    public User createUser(User user) {
//        return repository.createUserInStorage(user);
    return repository.save(user);
    }

    @Override
    @Transactional(readOnly = false)
    public User updateUser(Long userId, User user) {
//        return repository.updateUserInStorage(userId, user);
    return repository.save(user);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUserById(Long userId) {
//        repository.deleteUserByIdInStorage(userId);
    repository.deleteById(userId);
    }
}
