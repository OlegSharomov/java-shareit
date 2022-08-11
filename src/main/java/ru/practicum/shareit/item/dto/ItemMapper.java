package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public Item toItem(ItemDto itemDtoController) {
        return Item.builder()
                .id(itemDtoController.getId())
                .name(itemDtoController.getName())
                .description(itemDtoController.getDescription())
                .available(itemDtoController.getAvailable())
//                .owner(itemDtoController.getOwner())
                .build();
    }

    public ItemDtoAnswer toItemDtoAnswer(Item itemDtoService) {
        return ItemDtoAnswer.builder()
                .id(itemDtoService.getId())
                .name(itemDtoService.getName())
                .description(itemDtoService.getDescription())
                .available(itemDtoService.getAvailable())
                .build();
    }
}