package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
//    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
//    @NotNull(message = "Отсутствует описание вещи")
    private String description;
//    @NotNull(message = "Отсутствует статус доступности вещи для аренды")
    private Boolean available;
    private User owner;
    private Long requestId;
}