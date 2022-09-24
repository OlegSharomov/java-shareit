package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private Long id;
    @NotBlank(message = "The user name is blank")
    private String name;
    @NotNull(message = "The user's email is missing")
    @Email(message = "Email is not correct")
    private String email;
}
