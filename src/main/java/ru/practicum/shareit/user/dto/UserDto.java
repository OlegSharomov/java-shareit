package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя отсутствует")
    private String name;
    @NotNull(message = "Email пользователя отсутствует")
    @Email(message = "Email не проходит проверку")
    private String email;

    public UserDto() {
    }
}
