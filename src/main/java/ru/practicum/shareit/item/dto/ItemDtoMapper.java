package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    public static ItemDtoService itemDtoControllerToItemDtoService(ItemDtoController itemDtoController) {
        return ItemDtoService.builder()
                .id(itemDtoController.getId())
                .name(itemDtoController.getName())
                .description(itemDtoController.getDescription())
                .available(itemDtoController.getAvailable())
                .owner(itemDtoController.getOwner())
                .build();
    }

    public static Item itemDtoServiceToItem(ItemDtoService itemDtoService) {
        return Item.builder()
                .id(itemDtoService.getId())
                .name(itemDtoService.getName())
                .description(itemDtoService.getDescription())
                .available(itemDtoService.getAvailable())
                .owner(itemDtoService.getOwner())
                .build();
    }

    public static ItemDtoService itemToItemDtoService(Item item) {
        return ItemDtoService.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    public static ItemDtoControllerForAnswer itemDtoServiceToItemDtoControllerForAnswer(ItemDtoService itemDtoService) {
        return ItemDtoControllerForAnswer.builder()
                .id(itemDtoService.getId())
                .name(itemDtoService.getName())
                .description(itemDtoService.getDescription())
                .available(itemDtoService.getAvailable())
                .build();
    }
}
