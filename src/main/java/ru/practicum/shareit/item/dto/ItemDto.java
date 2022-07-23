package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private Long requestId;

    public ItemDto(String name, String description, Boolean available, Long requestId) {
    }
}
