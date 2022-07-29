package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForAnswer;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
class UserController {
    private final UserService service;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDtoForAnswer> getAllUsers() {
        log.info("Получен запрос GET/users");
        List<User> users = service.getAllUsers();
        return users.stream().map(userMapper::toUserDtoForAnswer).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDtoForAnswer getUserById(@PathVariable Long userId) {
        log.info("Получен запрос GET/users/{}", userId);
        User user = service.getUserById(userId);
        return userMapper.toUserDtoForAnswer(user);
    }

    @PostMapping
    public UserDtoForAnswer createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST/users с переданным телом: {}", userDto);
        User user = userMapper.toUser(userDto);
        User userForAnswer = service.createUser(user);
        return userMapper.toUserDtoForAnswer(userForAnswer);
    }

    @PatchMapping("/{userId}")
    public UserDtoForAnswer updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH/users/{} с переданным телом: {}", userId, userDto);
        User user = userMapper.toUser(userDto);
        User userForAnswer = service.updateUser(userId, user);
        return userMapper.toUserDtoForAnswer(userForAnswer);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос DELETE/users/{}", userId);
        service.deleteUserById(userId);
    }
}