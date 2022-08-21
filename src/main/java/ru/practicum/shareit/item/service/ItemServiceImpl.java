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
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Item getItemById(Long userId, Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            throw new NotFoundException(String.format("Вещь с переданным id = %d отсутствует в хранилище", itemId));
        }
        return optionalItem.get();
    }

    @Override
    @Transactional
    public ItemDtoAnswerFull getItemByIdAndConvertedToAnswer(Long userId, Long itemId) {
        Item item = getItemById(userId, itemId);
        ItemDtoAnswerFull answer = collectItemWithBookings(item, userId);
        return answer;
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
    public List<Item> getAllItemsOfUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional
    public List<ItemDtoAnswerFull> getAllItemsOfUserAndConvertedToAnswer(Long userId) {
        List<Item> items = getAllItemsOfUser(userId);
        return items.stream().map(x -> collectItemWithBookings(x, userId)).collect(Collectors.toList());
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

    //POST /items/{itemId}/comment
    /*Не забудьте добавить проверку, что пользователь, который пишет комментарий, действительно брал вещь в аренду.
     *Отзыв может оставить только тот пользователь, который брал эту вещь в аренду, и только после окончания срока аренды.*/
    @Override
    @Transactional(readOnly = false)
    public Comment createComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusAndEndBefore(itemId, APPROVED,
                LocalDateTime.now());
        Optional<Booking> optionalBooking = bookings.stream().
                filter(x -> x.getBooker().getId().equals(userId) && x.getItem().getId().equals(itemId))
                .findFirst();
        if (!optionalBooking.isPresent()) {
            throw new ValidationException("Пользователь не может оставить отзыв об этой вещи, " +
                    "т.к. отсутстуют данные о бронировании");
        }
        Booking booking = optionalBooking.get();
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(booking.getBooker())
                .created(LocalDateTime.now())
                .item(booking.getItem())
                .build();
        return commentRepository.save(comment);
    }
}