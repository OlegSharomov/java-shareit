package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class User {
    @Positive
    private final Long id;
    @NotBlank
    private String name;
    @Email
    private String email;   // два пользователя не могут иметь одинаковый адрес электронной почты
}