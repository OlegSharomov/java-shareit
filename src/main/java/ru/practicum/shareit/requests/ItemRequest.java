package ru.practicum.shareit.requests;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    @Positive
    private final Long id;
    @NotBlank
    private String description;
    private User requestor;     // — пользователь, создавший запрос
    private final LocalDateTime created;    // — дата и время создания запроса
}