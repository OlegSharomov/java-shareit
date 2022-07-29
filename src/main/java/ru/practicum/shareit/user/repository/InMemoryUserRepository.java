package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long getNewUserId() {
        return ++id;
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserByIdFromStorage(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с переданным id = %d отсутствует в хранилище", userId));
        }
        return users.get(userId);
    }

    @Override
    public User createUserInStorage(User user) {
        boolean isEmailAlreadyExistsInRepository = users.values().stream()
                .anyMatch(x -> x.getEmail().equals(user.getEmail()));
        if (isEmailAlreadyExistsInRepository) {
            throw new DuplicateException("Пользователь с таким email уже существует в хранилище");
        }
        user.setId(getNewUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUserInStorage(Long userId, User user) {
        User userFromRepository = users.get(userId);
        if (user.getName() != null && !user.getName().equals(userFromRepository.getName())) {
            userFromRepository.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(userFromRepository.getEmail())) {
            boolean isEmailAlreadyExistsInRepository = users.values().stream()
                    .filter(x -> !x.equals(userFromRepository))
                    .anyMatch(x -> x.getEmail().equals(user.getEmail()));
            if (isEmailAlreadyExistsInRepository) {
                throw new DuplicateException("Пользователь с таким email уже существует в хранилище");
            }
            userFromRepository.setEmail(user.getEmail());
        }
        return userFromRepository;
    }

    @Override
    public void deleteUserByIdInStorage(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с переданным id = %d отсутствует в хранилище", userId));
        }
        users.remove(userId);
    }

    @Override
    public boolean isUserExistsById(Long userId) {
        return users.containsKey(userId);
    }
}
