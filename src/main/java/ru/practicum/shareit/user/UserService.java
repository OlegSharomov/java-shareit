package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<UserDtoService> getAllUsers() {
        return repository.getAllUsersFromStorage();
    }

    public UserDtoService getUserById(Long userId) {
        return repository.getUserByIdFromStorage(userId);
    }

    public UserDtoService createUser(UserDtoService userDtoService) {
        return repository.createUserInStorage(userDtoService);
    }

    public UserDtoService updateUser(Long userId, UserDtoService userDtoService) {
        return repository.updateUserInStorage(userId, userDtoService);
    }

    public void deleteUserById(Long userId) {
        repository.deleteUserByIdInStorage(userId);
    }
}
