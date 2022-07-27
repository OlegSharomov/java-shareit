package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoControllerForAnswer {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
