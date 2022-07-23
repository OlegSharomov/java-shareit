package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Item {
    @Positive
    private final Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;  // — статус о том, доступна или нет вещь для аренды. Проставляется только владельцем!
    private User owner;         // — владелец вещи
    private ItemRequest request;    /* — если вещь была создана по запросу другого пользователя, то в этом
    ItemRequest или id?                     поле будет храниться ссылка на соответствующий запрос */


    public Boolean isAvailable() {
        return available;
    }
}