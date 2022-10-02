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
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDtoAnswerFull getItemById(Long userId, Long itemId) {
        checkExistenceUserInRepositoryById(userId);
        Item item = getEntityItemByIdFromStorage(itemId);
        return collectItemWithBookingsAndComments(item, userId);
    }

    @Override
    @Transactional
    public Item getEntityItemByIdFromStorage(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("The item with id = %d is missing from the storage", itemId)));
    }

    @Transactional
    public ItemDtoAnswerFull collectItemWithBookingsAndComments(Item item, Long userId) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = comments.stream()
                .map(x -> commentMapper.toCommentDto(x, x.getAuthor()))
                .collect(Collectors.toList());
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
        checkExistenceUserInRepositoryById(userId);
        List<Item> items = getAllEntityItemsOfUserFromStorage(userId);
        return items.stream()
                .map(x -> collectItemWithBookingsAndComments(x, userId))
                .sorted(Comparator.comparingLong(ItemDtoAnswerFull::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Item> getAllEntityItemsOfUserFromStorage(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemDtoAnswer createItem(Long userId, ItemDto itemDto) {
        checkExistenceUserInRepositoryById(userId);
        if (itemDto.getId() != null && Boolean.TRUE.equals(isItemExists(itemDto.getId()))) {
            throw new ValidationException("Items can be changed only through the 'PATCH' method");
        }
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("The passed item request id was not found"));
        }
        if (itemRequest != null && itemRequest.getRequestor().getId().equals(userId)) {
            throw new ValidationException("The user who created the request cannot offer items for him");
        }
        Item item = itemMapper.toItem(itemDto, itemRequest);
        User owner = userService.getEntityUserByIdFromStorage(userId);
        item.setOwner(owner);
        itemRepository.save(item);
        return itemMapper.toItemDtoAnswer(item, item.getRequest());
    }

    @Override
    @Transactional(readOnly = false)
    public ItemDtoAnswer updateItem(Long userId, Long itemId, ItemDto itemDto) {
        checkExistenceUserInRepositoryById(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException(
                    String.format("Passed id = %d of the item not found", itemDto.getRequestId())));
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String
                .format("Item with id = %d not found", itemId)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new OwnerVerificationException("Editing access is restricted. Only owner of the item can edit it.");
        }
        itemMapper.updateItemFromDto(itemDto, item, itemRequest);
        itemRepository.save(item);
        return itemMapper.toItemDtoAnswer(item, item.getRequest());
    }

    @Override
    @Transactional
    public List<ItemDtoAnswer> searchForItemsByQueryText(String text) {
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        return items.stream()
                .map(x -> itemMapper.toItemDtoAnswer(x, x.getRequest()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        checkExistenceUserInRepositoryById(userId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Item with id = %d not found", itemId));
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusAndEndBefore(itemId, APPROVED,
                LocalDateTime.now());
        Booking booking = bookings.stream()
                .filter(x -> x.getBooker().getId().equals(userId) && x.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("The user cannot create a comment about this thing, " +
                        "because there is no booking data"));
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(booking.getBooker())
                .created(LocalDateTime.now())
                .item(booking.getItem())
                .build();
        Comment answer = commentRepository.save(comment);
        return commentMapper.toCommentDto(answer, answer.getAuthor());
    }

    public boolean isItemExists(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    public void checkExistenceUserInRepositoryById(Long userId) {
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }
}