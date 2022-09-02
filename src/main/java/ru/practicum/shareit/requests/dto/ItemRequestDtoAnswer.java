package ru.practicum.shareit.requests.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequestDtoAnswer {
    private Long id;
    private String description;
//    private Long requestId;
    private LocalDateTime created;
}
