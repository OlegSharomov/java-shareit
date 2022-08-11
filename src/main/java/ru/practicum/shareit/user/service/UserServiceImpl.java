package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperForPatch;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapperForPatch userMapperForPatch;

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
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователь с переданным id = %d отсутствует в хранилище", userId));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public User createUser(User user) {
//        return repository.createUserInStorage(user);
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = false)
    public User updateUser(Long userId, UserDto userDto) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userMapperForPatch.updateUserFromDto(userDto, user);
//        return repository.updateUserInStorage(userId, user);
            return repository.save(user);
        } else {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUserById(Long userId) {
//        repository.deleteUserByIdInStorage(userId);
        repository.deleteById(userId);
    }
}
