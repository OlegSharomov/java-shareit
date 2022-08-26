package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDtoAnswerFull getItemById(Long userId, Long itemId) {
        Item item = getEntityItemByIdFromStorage(userId, itemId);
        return collectItemWithBookings(item, userId);
    }

    @Override
    @Transactional
    public Item getEntityItemByIdFromStorage(Long userId, Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId)));
    }

    @Transactional
    ItemDtoAnswerFull collectItemWithBookings(Item item, Long userId) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        if (!item.getOwner().getId().equals(userId)) {
            return itemMapper.toItemDtoAnswerFull(item, null, null, commentsDto);
        }
        Booking last = bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(item.getId(), LocalDateTime.now());
        Booking next = bookingRepository.findByItemIdAndStartIsAfterOrderByStart(item.getId(), LocalDateTime.now());
        return itemMapper.toItemDtoAnswerFull(item, bookingMapper.toBookingDtoWithBookerId(last),
                bookingMapper.toBookingDtoWithBookerId(next), commentsDto);
    }

    @Override
    @Transactional
    public List<ItemDtoAnswerFull> getAllItemsOfUser(Long userId) {
        List<Item> items = getAllEntityItemsOfUserFromStorage(userId);
        return items.stream().map(x -> collectItemWithBookings(x, userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Item> getAllEntityItemsOfUserFromStorage(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemDtoAnswer createItem(Long userId, ItemDto itemDto) {
        if (itemDto.getId() != null && isItemExists(itemDto.getId())) {
            throw new ValidationException("Данные вещи можно изменять только через метод PATCH");
        }
        Item item = itemMapper.toItem(itemDto);
        User owner = userService.getEntityUserByIdFromStorage(userId);
        item.setOwner(owner);
        itemRepository.save(item);
        return itemMapper.toItemDtoAnswer(item);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemDtoAnswer updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (itemDto.getId() != null && !itemDto.getId().equals(itemId)) {
            throw new ValidationException("Id вещи изменять нельзя");
        }
        if (itemDto.getName() != null && itemDto.getName().trim().isEmpty()) {
            throw new ValidationException("Название вещи не должно быть пустым");
        }
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с переданным id не найден");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String
                .format("Вещь с переданным id = %d не найдена", itemId)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new OwnerVerificationException("Доступ к редактированию ограничен. " +
                    "Редактировать вещь может только её владелец.");
        }
        itemMapper.updateItemFromDto(itemDto, item);
        itemRepository.save(item);
        return itemMapper.toItemDtoAnswer(item);
    }

    @Override
    @Transactional
    public List<ItemDtoAnswer> searchForItemsByQueryText(String text) {
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        return items.stream().map(itemMapper::toItemDtoAnswer).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusAndEndBefore(itemId, APPROVED,
                LocalDateTime.now());
        Booking booking = bookings.stream()
                .filter(x -> x.getBooker().getId().equals(userId) && x.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь не может оставить отзыв об этой вещи, " +
                        "т.к. отсутстуют данные о бронировании"));
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(booking.getBooker())
                .created(LocalDateTime.now())
                .item(booking.getItem())
                .build();
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    public boolean isItemExists(Long itemId) {
        return itemRepository.existsById(itemId);
    }
}