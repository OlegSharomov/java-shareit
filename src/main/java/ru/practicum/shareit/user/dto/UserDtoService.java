package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDtoService {
    private  Long id;
    @NotBlank
    private  String name;
    @NotNull
    @Email
    private  String email;   // два пользователя не могут иметь одинаковый адрес электронной почты
}
