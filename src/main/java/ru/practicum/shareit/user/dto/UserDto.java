package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private  Long id;
    private  String name;
    private  String email;   // два пользователя не могут иметь одинаковый адрес электронной почты

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
