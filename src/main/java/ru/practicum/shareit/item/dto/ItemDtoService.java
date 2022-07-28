package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

@Data
@Builder
public class ItemDtoService {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequest request;
    private Long requestId;

    public Boolean isAvailable() {
        return available;
    }

}
