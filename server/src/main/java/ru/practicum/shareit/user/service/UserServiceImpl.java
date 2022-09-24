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
public class UserServiceImpl implements UserService {
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
                .format("User with id = %d not found", userId)));
        return userMapper.toUserDtoAnswer(user);
    }

    @Override
    @Transactional
    public User getEntityUserByIdFromStorage(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String
                .format("User with id = %d not found", userId)));
    }

    @Override
    @Transactional(readOnly = false)
    public UserDtoAnswer createUser(UserDto userDto) {
        if (userDto.getId() != null && Boolean.TRUE.equals(isUserExists(userDto.getId()))) {
            throw new ValidationException("You can change user data only through the 'PATCH' method");
        }
        User user = userMapper.toUser(userDto);
        userRepository.save(user);
        return userMapper.toUserDtoAnswer(user);
    }

    @Override
    @Transactional(readOnly = false)
    public UserDtoAnswer updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %d not found", userId)));
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
