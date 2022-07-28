package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDtoController {
    private Long id;
    @NotBlank(message = "произошла ошибка. Название вещи не должно быть пустым")
    private String name;
    @NotNull(message = "произошла ошибка. Отсутствует описание вещи")
    private String description;
    @NotNull(message = "произошла ошибка. Отсутствует статус доступности вещи для аренды")
    private Boolean available;
    private Long owner;
    private ItemRequest request;
    private Long requestId;

    public Boolean isAvailable() {
        return available;
    }
}
