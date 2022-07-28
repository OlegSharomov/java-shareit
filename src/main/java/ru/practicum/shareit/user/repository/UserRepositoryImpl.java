package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static Long id = 0L;

    private static Long getNewUserId() {
        return ++id;
    }

    @Override
    public List<UserDtoService> getAllUsersFromStorage() {
        return users.values().stream().map(UserDtoMapper::userToUserDtoService).collect(Collectors.toList());
    }

    @Override
    public UserDtoService getUserByIdFromStorage(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с переданным id = %d отсутствует в хранилище", userId));
        }
        return UserDtoMapper.userToUserDtoService(users.get(userId));
    }

    @Override
    public UserDtoService createUserInStorage(UserDtoService userDtoService) {
        boolean isEmailAlreadyExistsInRepository = users.values().stream()
                .anyMatch(x -> x.getEmail().equals(userDtoService.getEmail()));
        if (isEmailAlreadyExistsInRepository) {
            throw new DuplicateException("Пользователь с таким email уже существует в хранилище");
        }
        userDtoService.setId(getNewUserId());
        User user = UserDtoMapper.userDtoServiceToUser(userDtoService);
        users.put(user.getId(), user);
        return UserDtoMapper.userToUserDtoService(user);
    }

    @Override
    public UserDtoService updateUserInStorage(Long userId, UserDtoService userDtoService) {
        User userFromRepository = users.get(userId);
        if (userDtoService.getName() != null && !userDtoService.getName().equals(userFromRepository.getName())) {
            userFromRepository.setName(userDtoService.getName());
        }
        if (userDtoService.getEmail() != null && !userDtoService.getEmail().equals(userFromRepository.getEmail())) {
            boolean isEmailAlreadyExistsInRepository = users.values().stream()
                    .filter(x -> !x.equals(userFromRepository))
                    .anyMatch(x -> x.getEmail().equals(userDtoService.getEmail()));
            if (isEmailAlreadyExistsInRepository) {
                throw new DuplicateException("Пользователь с таким email уже существует в хранилище");
            }
            userFromRepository.setEmail(userDtoService.getEmail());
        }
        return UserDtoMapper.userToUserDtoService(userFromRepository);
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
