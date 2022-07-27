package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Item {
    private final Long id;
    private String name;
    private String description;
    private Boolean available;  // — статус о том, доступна или нет вещь для аренды. Проставляется только владельцем!
    private User owner;         // — владелец вещи
    private ItemRequest request;    /* — если вещь была создана по запросу другого пользователя, то в этом
    ItemRequest или id?                     поле будет храниться ссылка на соответствующий запрос */

    public Boolean isAvailable() {
        return available;
    }
}