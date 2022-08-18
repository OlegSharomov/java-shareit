package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public Item getItemById(Long userId, Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            throw new NotFoundException(String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId));
        }
        return optionalItem.get();
    }

    @Transactional
    public ItemDtoAnswerFull getAndConvertedByAnswer(Long userId, Long itemId) {
        Item item = getItemById(userId, itemId);
        if (item.getOwner().getId().equals(userId)) {
            Booking last = bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(itemId, LocalDateTime.now());
            Booking next = bookingRepository.findByItemIdAndStartIsAfterOrderByStart(itemId, LocalDateTime.now());
            return itemMapper.toItemDtoAnswerFull(item, bookingMapper.toBookingDtoWithBookerId(last),
                    bookingMapper.toBookingDtoWithBookerId(next));
        } else {
            return itemMapper.toItemDtoAnswerFull(item, null, null);
        }
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
            throw new NotFoundException("Пользователь с переданным id не найден");
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
            throw new NotFoundException(String.format("Вещь с переданным id = %d не найдена", itemId));
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