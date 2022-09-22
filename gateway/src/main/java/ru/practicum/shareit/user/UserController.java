package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Received a request: GET/users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive(message = "id must be positive")
                                              @PathVariable Long userId) {
        log.info("Received a request: GET/users/{}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Received a request: POST/users with request body: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive(message = "id must be positive")
                                             @PathVariable Long userId,
                                             @RequestBody UserRequestDto userDto) {
        log.info("Received a request: PATCH/users/{} with request body: {}", userId, userDto);
        if (userDto.getId() != null && !userDto.getId().equals(userId)) {
            throw new ValidationException("Нельзя изменять id пользователя");
        }
        if (userDto.getName() != null && userDto.getName().trim().isEmpty()) {
            throw new ValidationException("Поле name должно быть заполнено");
        }
        if (userDto.getEmail() != null && userDto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Поле email должно быть заполнено");
        }
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@Positive(message = "id must be positive")
                                                 @PathVariable Long userId) {
        log.info("Received a request: DELETE/users/{}", userId);
        return userClient.deleteUserById(userId);
    }

}
