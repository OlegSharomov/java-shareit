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
import ru.practicum.shareit.user.dto.UserDtoController;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoService;
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

    @GetMapping
    public List<UserDtoController> getAllUsers() {
        log.info("Получен запрос GET/users");
        List<UserDtoService> users = service.getAllUsers();
        return users.stream().map(UserDtoMapper::userDtoServiceToUserDtoController).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDtoController getUserById(@PathVariable Long userId) {
        log.info("Получен запрос GET/users/{}", userId);
        UserDtoService userDtoService = service.getUserById(userId);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoService);
    }

    @PostMapping
    public UserDtoController createUser(@Valid @RequestBody UserDtoController userDtoController) {
        log.info("Получен запрос POST/users с переданным телом: {}", userDtoController);
        UserDtoService userDtoService = UserDtoMapper.userDtoControllerToUserDtoService(userDtoController);
        UserDtoService userDtoServiceForAnswer = service.createUser(userDtoService);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoServiceForAnswer);
    }

    @PatchMapping("/{userId}")
    public UserDtoController updateUser(@PathVariable Long userId, @RequestBody UserDtoController userDtoController) {
        log.info("Получен запрос PATCH/users/{} с переданным телом: {}", userId, userDtoController);
        UserDtoService userDtoService = UserDtoMapper.userDtoControllerToUserDtoService(userDtoController);
        UserDtoService userDtoServiceForAnswer = service.updateUser(userId, userDtoService);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoServiceForAnswer);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос DELETE/users/{}", userId);
        service.deleteUserById(userId);
    }
}
