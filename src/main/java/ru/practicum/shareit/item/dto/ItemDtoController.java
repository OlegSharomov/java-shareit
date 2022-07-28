package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDtoController {
    private Long id;
    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
    @NotNull(message = "Отсутствует описание вещи")
    private String description;
    @NotNull(message = "Отсутствует статус доступности вещи для аренды")
    private Boolean available;
    private Long owner;
    private ItemRequest request;
    private Long requestId;

    public Boolean isAvailable() {
        return available;
    }
}
