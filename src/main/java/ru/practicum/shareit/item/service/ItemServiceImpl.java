package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item getItemById(Long userId, Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            throw new ItemNotFoundException(String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId));
        }
        return optionalItem.get();
    }

    @Override
    @Transactional
    public List<Item> getAllItemsOfUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional(readOnly = false)
    public Item createItem(Long userId, Item item) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = false)
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!userService.isUserExists(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (!item.getOwner().getId().equals(userId)) {
                throw new OwnerVerificationException("Доступ к редактированию ограничен. " +
                        "Редактировать вещь может только её владелец.");
            }
            itemMapper.updateItemFromDto(itemDto, item);
            return itemRepository.save(item);
        } else {
            throw new ItemNotFoundException(String.format("Вещь с переданным id = %d не найдена", itemId));
        }
    }

    @Override
    @Transactional
    public List<Item> searchForItemsByQueryText(String text) {
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
    }
}