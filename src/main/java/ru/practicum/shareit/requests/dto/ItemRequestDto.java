package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private final Long id;
    private String description;
    private User requestor;
    private final LocalDateTime created;
}
