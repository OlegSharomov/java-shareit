package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoController;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDtoController> getAllUsers() {
        List<UserDtoService> users = service.getAllUsers();
        return users.stream().map(UserDtoMapper::userDtoServiceToUserDtoController).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDtoController getUserById(@PathVariable Long userId) {
        UserDtoService userDtoService = service.getUserById(userId);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoService);
    }

    @PostMapping
    public UserDtoController createUser(@Valid @RequestBody UserDtoController userDtoController) {
        UserDtoService userDtoService = UserDtoMapper.userDtoControllerToUserDtoService(userDtoController);
        UserDtoService userDtoServiceForAnswer = service.createUser(userDtoService);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoServiceForAnswer);
    }

    @PatchMapping("/{userId}")
    public UserDtoController updateUser(@PathVariable Long userId, @RequestBody UserDtoController userDtoController) {
        UserDtoService userDtoService = UserDtoMapper.userDtoControllerToUserDtoService(userDtoController);
        UserDtoService userDtoServiceForAnswer = service.updateUser(userId, userDtoService);
        return UserDtoMapper.userDtoServiceToUserDtoController(userDtoServiceForAnswer);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        service.deleteUserById(userId);
    }
}
