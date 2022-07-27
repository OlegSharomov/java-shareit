package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDtoController {
    private  Long id;
    @NotBlank(message = "произошла ошибка. Имя пользователя отсутствует")
    private  String name;
    @NotNull(message = "произошла ошибка. Email пользователя отсутствует")
    @Email(message = "произошла ошибка. Email не проходит проверку")
    private  String email;   // два пользователя не могут иметь одинаковый адрес электронной почты
}
