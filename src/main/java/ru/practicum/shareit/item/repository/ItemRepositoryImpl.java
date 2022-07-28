package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long id = 0L;

    private static Long getNewItemId() {
        return ++id;
    }

    @Override
    public ItemDtoService getItemByIdFromStorage(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(
                    String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId));
        }
        return ItemDtoMapper.itemToItemDtoService(items.get(itemId));
    }

    @Override
    public List<ItemDtoService> getAllItemsOfUserFromStorage(Long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .map(ItemDtoMapper::itemToItemDtoService)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoService createItemInStorage(Long userId, ItemDtoService itemDtoService) {
        itemDtoService.setId(getNewItemId());
        itemDtoService.setOwner(userId);
        Item item = ItemDtoMapper.itemDtoServiceToItem(itemDtoService);
        items.put(item.getId(), item);
        return ItemDtoMapper.itemToItemDtoService(item);
    }

    @Override
    public ItemDtoService updateItemInStorage(Long userId, Long itemId, ItemDtoService itemDtoService) {
        Item itemFromRepository = items.get(itemId);
        if (!itemFromRepository.getOwner().equals(userId)) {
            throw new OwnerVerificationException("Доступ к редактированию ограничен. " +
                    "Редактировать вещь может только её владелец.");
        }
        if (itemDtoService.getName() != null && !itemDtoService.getName().equals(itemFromRepository.getName())) {
            itemFromRepository.setName(itemDtoService.getName());
        }
        if (itemDtoService.getDescription() != null
                && !itemDtoService.getDescription().equals(itemFromRepository.getDescription())) {
            itemFromRepository.setDescription(itemDtoService.getDescription());
        }
        if (itemDtoService.getAvailable() != null
                && !itemDtoService.getAvailable().equals(itemFromRepository.getAvailable())) {
            itemFromRepository.setAvailable(itemDtoService.getAvailable());
        }
        return ItemDtoMapper.itemToItemDtoService(itemFromRepository);
    }

    @Override
    public List<ItemDtoService> searchForItemsByQueryTextFromStorage(String[] words) {
        return items.values().stream()
                .filter(e -> containsWords(e, words) && e.getAvailable().equals(true))
                .map(ItemDtoMapper::itemToItemDtoService)
                .collect(Collectors.toList());
    }

    private boolean containsWords(Item item, String[] words) {
        return Arrays.stream(words)
                .anyMatch(w -> item.getName().toLowerCase(Locale.ROOT).contains(w.toLowerCase())
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(w.toLowerCase()));
    }
}
