package ru.practicum.shareit.requests.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestDtoAnswerFull {
    private Long id;
    private String description;
//    private Long requestId;
    private LocalDateTime created;
    private List<Item> items;
}
