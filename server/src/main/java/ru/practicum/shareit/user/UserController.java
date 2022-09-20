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
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDtoAnswer> getAllUsers() {
        log.info("Получен запрос GET/users");
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDtoAnswer getUserById(@PathVariable Long userId) {
        log.info("Получен запрос GET/users/{}", userId);
        return service.getUserById(userId);
    }

    @PostMapping
    public UserDtoAnswer createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST/users с переданным телом: {}", userDto);
        return service.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDtoAnswer updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH/users/{} с переданным телом: {}", userId, userDto);
        return service.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос DELETE/users/{}", userId);
        service.deleteUserById(userId);
    }
}