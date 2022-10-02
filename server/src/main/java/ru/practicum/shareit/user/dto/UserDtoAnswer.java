package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDtoAnswer {
    Long id;
    String name;
    String email;
}
