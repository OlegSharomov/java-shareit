package ru.practicum.shareit.user;

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
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static Long id = 0L;

    private static Long getNewUserId() {
        return ++id;
    }

    public List<UserDtoService> getAllUsersFromStorage() {
        return users.values().stream().map(UserDtoMapper::userToUserDtoService).collect(Collectors.toList());
    }

    public UserDtoService getUserByIdFromStorage(Long userId) {
        return UserDtoMapper.userToUserDtoService(users.get(userId));
    }

    public UserDtoService createUserInStorage(UserDtoService userDtoService) {
        boolean isContains = users.values().stream().anyMatch(x -> x.getEmail().equals(userDtoService.getEmail()));
        if (!isContains) {
            userDtoService.setId(getNewUserId());
            User user = UserDtoMapper.userDtoServiceToUser(userDtoService);
            users.put(user.getId(), user);
            return UserDtoMapper.userToUserDtoService(user);
        } else {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }
    }

    public UserDtoService updateUserInStorage(Long userId, UserDtoService userDtoService) {
        User userFromRepository = users.get(userId);
        if (userDtoService.getName() != null && !userDtoService.getName().equals(userFromRepository.getName())) {
            userFromRepository.setName(userDtoService.getName());
        }
        if (userDtoService.getEmail() != null && !userDtoService.getEmail().equals(userFromRepository.getEmail())) {
            boolean isContainsEmail = users.values().stream()
                    .filter(x -> !x.equals(userFromRepository))
                    .anyMatch(x -> x.getEmail().equals(userDtoService.getEmail()));
            if (isContainsEmail) {
                throw new DuplicateException("Пользователь с таким email уже существует в хранилище");
            }
            userFromRepository.setEmail(userDtoService.getEmail());
        }
        return UserDtoMapper.userToUserDtoService(userFromRepository);
    }

    public void deleteUserByIdInStorage(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(
                    String.format("Пользователь с переданным id = %d отсутствует в хранилище", userId));
        }
        users.remove(userId);
    }

    public boolean userIsContainsById(Long userId){
        return users.containsKey(userId);
    }
}
