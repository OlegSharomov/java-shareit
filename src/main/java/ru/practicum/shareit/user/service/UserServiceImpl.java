package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public List<UserDtoAnswer> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDtoAnswer).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDtoAnswer getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String
                .format("Пользователь с переданным id = %d отсутствует в хранилище", userId)));
        return userMapper.toUserDtoAnswer(user);
    }

    @Override
    @Transactional
    public User getEntityUserByIdFromStorage(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String
                .format("Пользователь с переданным id = %d отсутствует в хранилище", userId)));
    }

    @Override
    @Transactional(readOnly = false)
    public UserDtoAnswer createUser(UserDto userDto) {
        if (userDto.getId() != null && isUserExists(userDto.getId())) {
            throw new ValidationException("Изменять данные пользователя можно только через метод PATCH");
        }
        User user = userMapper.toUser(userDto);
        userRepository.save(user);
        return userMapper.toUserDtoAnswer(user);
    }

    @Override
    @Transactional(readOnly = false)
    public UserDtoAnswer updateUser(Long userId, UserDto userDto) {
        if (userDto.getId() != null && !userDto.getId().equals(userId)) {
            throw new ValidationException("Нельзя изменять id пользователя");
        }
        if (userDto.getName() != null && userDto.getName().trim().isEmpty()) {
            throw new ValidationException("Поле name должно быть заполнено");
        }
        if (userDto.getEmail() != null && userDto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Поле email должно быть заполнено");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с переданным id не найден"));
        userMapper.updateUserFromDto(userDto, user);
        return userMapper.toUserDtoAnswer(user);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }
}
