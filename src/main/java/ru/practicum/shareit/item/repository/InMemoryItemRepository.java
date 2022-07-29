package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long getNewItemId() {
        return ++id;
    }

    @Override
    public Item getItemByIdFromStorage(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(
                    String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemsOfUserFromStorage(Long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item createItemInStorage(Long userId, Item item) {
        item.setId(getNewItemId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItemInStorage(Long userId, Long itemId, Item item) {
        Item itemFromRepository = items.get(itemId);
        if (!itemFromRepository.getOwner().equals(userId)) {
            throw new OwnerVerificationException("Доступ к редактированию ограничен. " +
                    "Редактировать вещь может только её владелец.");
        }
        if (item.getName() != null && !item.getName().equals(itemFromRepository.getName())) {
            itemFromRepository.setName(item.getName());
        }
        if (item.getDescription() != null
                && !item.getDescription().equals(itemFromRepository.getDescription())) {
            itemFromRepository.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null
                && !item.getAvailable().equals(itemFromRepository.getAvailable())) {
            itemFromRepository.setAvailable(item.getAvailable());
        }
        return itemFromRepository;
    }

    @Override
    public List<Item> searchForItemsByQueryTextFromStorage(String[] words) {
        return items.values().stream()
                .filter(e -> containsWords(e, words) && e.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    private boolean containsWords(Item item, String[] words) {
        return Arrays.stream(words)
                .anyMatch(w -> item.getName().toLowerCase(Locale.ROOT).contains(w.toLowerCase())
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(w.toLowerCase()));
    }
}