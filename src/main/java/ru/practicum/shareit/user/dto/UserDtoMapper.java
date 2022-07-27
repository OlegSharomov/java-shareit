package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserDtoMapper {
    public static UserDtoService userToUserDtoService(User user) {
        return UserDtoService.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserDtoService userDtoControllerToUserDtoService(UserDtoController userDtoController) {
        return UserDtoService.builder()
                .id(userDtoController.getId())
                .name(userDtoController.getName())
                .email(userDtoController.getEmail())
                .build();
    }

    public static User userDtoServiceToUser(UserDtoService userDtoService) {
        return User.builder()
                .id(userDtoService.getId())
                .name(userDtoService.getName())
                .email(userDtoService.getEmail())
                .build();
    }

    public static UserDtoController userDtoServiceToUserDtoController(UserDtoService userDtoService) {
        return UserDtoController.builder()
                .id(userDtoService.getId())
                .name(userDtoService.getName())
                .email(userDtoService.getEmail())
                .build();
    }
}
