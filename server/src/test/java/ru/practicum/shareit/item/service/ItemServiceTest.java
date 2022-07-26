package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapperImpl;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserServiceImpl userService;
    @Spy
    private ItemMapperImpl itemMapper;
    @Spy
    private BookingMapperImpl bookingMapper;
    @Spy
    private CommentMapperImpl commentMapper;

    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
    User user3 = User.builder().id(3L).name("user3").email("user3@mail.ru").build();
    Item item1 = Item.builder().id(1L).name("item1").description("description of item1").available(true).owner(user1)
            .build();
    Item item2 = Item.builder().id(2L).name("item2").description("description of item2").available(true).owner(user1)
            .build();
    Comment comment1 = Comment.builder()
            .id(1L).text("Текст комментария1").author(user2).created(LocalDateTime.now().minusDays(3)).item(item1)
            .build();
    Comment comment2 = Comment.builder()
            .id(1L).text("Текст комментария2").author(user3).created(LocalDateTime.now().minusDays(3)).item(item1)
            .build();
    Booking lastBooking = Booking.builder().id(1L).start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1)).item(item1).booker(user2).status(APPROVED).build();
    Booking nextBooking = Booking.builder().id(1L).start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2)).item(item1).booker(user3).status(APPROVED).build();
    ItemRequest itemRequest1 = ItemRequest.builder().id(1L).description("Описание запроса вещи1").requestor(user2)
            .created(LocalDateTime.now().minusDays(5)).build();

    // getEntityItemByIdFromStorage
    @Test
    public void shouldReturnEntityWhenEntityExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Item item = itemService.getEntityItemByIdFromStorage(1L);
        assertEquals(item1, item);
    }

    @Test
    public void shouldThrowExceptionWhenEntityNotExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getEntityItemByIdFromStorage(1L));
        assertEquals("The item with id = 1 is missing from the storage", re.getMessage());
    }

    // collectItemWithBookingsAndComments
    @Test
    public void shouldReturnItemDtoWithoutCommentsAndBookings() {
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 2L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(Collections.emptyList()).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithComments() {
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 2L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true)
                .comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3))).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithoutCommentsAndBookingsForOwner() {
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);

        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(Collections.emptyList())
                .lastBooking(null).nextBooking(null).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithCommentsForOwner() {
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);

        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(null).nextBooking(null).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithCommentsAndNextBookingForOwner() {

        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(nextBooking);

        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(null).nextBooking(bookingMapper.toBookingDtoWithBookerId(nextBooking)).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithCommentsAndLastBookingForOwner() {

        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);

        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(bookingMapper.toBookingDtoWithBookerId(lastBooking)).nextBooking(null).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithCommentsAndBookingsForOwner() {

        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(nextBooking);

        ItemDtoAnswerFull result = itemService.collectItemWithBookingsAndComments(item1, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(bookingMapper.toBookingDtoWithBookerId(lastBooking))
                .nextBooking(bookingMapper.toBookingDtoWithBookerId(nextBooking)).build();
        assertEquals(itemToCheck, result);
    }

    // getItemById

    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L, 1L));
        assertEquals("User with id = 999 not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenItemNotExists() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 999L));
        assertEquals("The item with id = 999 is missing from the storage", re.getMessage());
    }

    @Test
    public void shouldReturnItemDtoWithCommentsForOwnerWhenWeCallGetItemById() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);

        ItemDtoAnswerFull result = itemService.getItemById(1L, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(null).nextBooking(null).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithCommentsAndBookingsForOwnerWhenWeCallGetItemById() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        ItemDtoAnswerFull result = itemService.getItemById(1L, 1L);
        ItemDtoAnswerFull itemToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(bookingMapper.toBookingDtoWithBookerId(lastBooking))
                .nextBooking(bookingMapper.toBookingDtoWithBookerId(nextBooking)).build();
        assertEquals(itemToCheck, result);
    }

    // getAllEntityItemsOfUserFromStorage
//    @Test
//    public void shouldReturnEmptyListWhenOwnerHasNoItems() {
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());
//        List<Item> result = itemService.getAllEntityItemsOfUserFromStorage(1L);
//        assertEquals(Collections.emptyList(), result);
//    }
//
//    @Test
//    public void shouldReturnListOfItemsWhenOwnerHasNoItems() {
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
//        List<Item> result = itemService.getAllEntityItemsOfUserFromStorage(1L);
//        assertEquals(List.of(item1, item2), result);
//    }
//
//    // getAllItemsOfUser
//    @Test
//    public void shouldThrowExceptionWhenWeCallGetAllItemsOfUser() {
//        when(userService.isUserExists(999L)).thenReturn(false);
//        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
//                () -> itemService.getAllItemsOfUser(999L));
//        assertEquals("Пользователь с переданным id = 999 не найден", re.getMessage());
//    }
//
//    @Test
//    public void shouldReturnEmptyListDtoWhenOwnerHasNoItems() {
//        when(userService.isUserExists(1L)).thenReturn(true);
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());
//        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
//        assertEquals(Collections.emptyList(), result);
//    }
//
//    @Test
//    public void shouldReturnListDtoWithCommentsWhenOwnerHasItems() {
//        when(userService.isUserExists(1L)).thenReturn(true);
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
//        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
//        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of(comment1, comment2));
//        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
//                .thenReturn(null);
//        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
//                .thenReturn(null);
//        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
//        ItemDtoAnswerFull itemToCheck1 = ItemDtoAnswerFull.builder().id(1L).name("item1")
//                .description("description of item1").available(true).comments(Collections.emptyList())
//                .lastBooking(null).nextBooking(null).build();
//        ItemDtoAnswerFull itemToCheck2 = ItemDtoAnswerFull.builder().id(2L).name("item2")
//                .description("description of item2").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
//                        commentMapper.toCommentDto(comment2, user3)))
//                .lastBooking(null).nextBooking(null).build();
//        assertEquals(List.of(itemToCheck1, itemToCheck2), result);
//    }

//    @Test
//    public void shouldReturnListDtoWithCommentAndBookingsWhenOwnerHasItems() {
//        when(userService.isUserExists(1L)).thenReturn(true);
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
//        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
//        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of(comment1, comment2));
//        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
//                .thenReturn(null);
//        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
//                .thenReturn(null);
//        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(2L), any(LocalDateTime.class)))
//                .thenReturn(lastBooking);
//        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(2L), any(LocalDateTime.class)))
//                .thenReturn(nextBooking);
//        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
//        ItemDtoAnswerFull itemToCheck1 = ItemDtoAnswerFull.builder().id(1L).name("item1")
//                .description("description of item1").available(true).comments(Collections.emptyList())
//                .lastBooking(null).nextBooking(null).build();
//        ItemDtoAnswerFull itemToCheck2 = ItemDtoAnswerFull.builder().id(2L).name("item2")
//                .description("description of item2").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
//                        commentMapper.toCommentDto(comment2, user3)))
//                .lastBooking(bookingMapper.toBookingDtoWithBookerId(lastBooking))
//                .nextBooking(bookingMapper.toBookingDtoWithBookerId(nextBooking)).build();
//        assertEquals(List.of(itemToCheck1, itemToCheck2), result);
//    }

    // createItem
    @Test
    public void shouldThrowExceptionWhenWeCallCreateItemAndUserNotExists() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(999L, ItemDto.builder()
                        .name("item1").description("description of item1").available(true).build()));
        assertEquals("User with id = 999 not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenItemIsAlreadyExist() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, ItemDto.builder()
                        .id(1L).name("item1Update").description("descriptionUpdate of item1").available(true).build()));
        assertEquals("Items can be changed only through the 'PATCH' method", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenItemRequestNotExist() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1").description("description of item1")
                .available(true).requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(false);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(1L, itemDto));
        assertEquals("The passed item request id was not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUserAndRequestorSame() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1").description("description of item1")
                .available(true).requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(false);
        when(itemRequestRepository.findById(1L)).thenReturn(
                Optional.of(ItemRequest.builder()
                        .id(1L).description("Описание для вещи 1").requestor(user1)
                        .created(LocalDateTime.now().minusDays(3)).build()));
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, itemDto));
        assertEquals("The user who created the request cannot offer items for him", re.getMessage());
    }


    @Test
    public void shouldReturnItemDtoWithRequestWhenWeCallCreateItem() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1").description("description of item1")
                .available(true).requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(false);
        when(itemRequestRepository.findById(1L)).thenReturn(
                Optional.of(ItemRequest.builder()
                        .id(1L).description("Описание для вещи 1").requestor(user2)
                        .created(LocalDateTime.now().minusDays(3)).build()));
        ItemDtoAnswer result = itemService.createItem(1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("description of item1")
                .available(true).requestId(1L).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithoutRequestWhenWeCallCreateItem() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1").description("description of item1")
                .available(true).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(false);
        ItemDtoAnswer result = itemService.createItem(1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("description of item1")
                .available(true).requestId(null).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    // updateItem
    @Test
    public void shouldThrowExceptionWhenWeTryUpdateItemAndUserNotExists() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").build();
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(999L, 1L, itemDto));
        assertEquals("User with id = 999 not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenWeTryUpdateItemWithItemRequestIdAndItemRequestNotExists() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));
        assertEquals("Passed id = 1 of the item not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenItemNotFound() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 999L, itemDto));
        assertEquals("Item with id = 999 not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUserAndOwnerDifferent() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").build();
        when(userService.isUserExists(2L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        RuntimeException re = Assertions.assertThrows(OwnerVerificationException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));
        assertEquals("Editing access is restricted. Only owner of the item can edit it.", re.getMessage());
    }

    @Test
    public void shouldReturnItemDtoWithDifferentNameWhenWeUpdateName() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1Update").description("description of item1")
                .available(true).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithDifferentDescriptionWhenWeUpdateDescription() {
        ItemDto itemDto = ItemDto.builder().description("descriptionUpdate of item1").build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("descriptionUpdate of item1")
                .available(true).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithDifferentAvailableWhenWeUpdateAvailable() {
        ItemDto itemDto = ItemDto.builder().available(false).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("description of item1")
                .available(false).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoWithRequestIdAndUpdatedDescriptionWhenWeUpdateDescription() {
        ItemDto itemDto = ItemDto.builder().description("descriptionUpdate of item1").requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("descriptionUpdate of item1")
                .available(true).requestId(1L).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldReturnDtoWithChangedParametersWhenWeUpdateManyParameters() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1Update").description("descriptionUpdate of item1")
                .available(false).requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1Update").description("descriptionUpdate of item1")
                .available(false).requestId(1L).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }

    // searchForItemsByQueryText
    @Test
    public void shouldReturnEmptyCollectionWhenWeFindByEmptyText() {
        List<ItemDtoAnswer> result = itemService.searchForItemsByQueryText(" ");
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void shouldReturnListDtoWhenWeFindItems() {
        when(itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue("item", "item"))
                .thenReturn(List.of(item1, item2));
        List<ItemDtoAnswer> result = itemService.searchForItemsByQueryText("item");
        List<ItemDtoAnswer> listToCheck = List.of(item1, item2).stream()
                .map(x -> itemMapper.toItemDtoAnswer(x, null)).collect(Collectors.toList());
        assertEquals(listToCheck, result);
    }

    // createComment
    @Test
    public void shouldThrowExceptionWhenNotExistsUserTryCreateComment() {
        CommentDto commentDto = CommentDto.builder().text("Хорошая вещь").build();
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> itemService.createComment(999L, 1L, commentDto));
        assertEquals("User with id = 999 not found", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUserTryCreateCommentByNotExistsItem() {
        CommentDto commentDto = CommentDto.builder().text("Хорошая вещь").build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(999L)).thenReturn(false);
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> itemService.createComment(1L, 999L, commentDto));
        assertEquals("Item with id = 999 not found", re.getMessage());
    }

    @Test
    public void should() {
        LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(), LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());
        Booking booking1 = Booking.builder().id(1L).start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3)).item(item1).booker(user1).status(APPROVED).build();
        CommentDto commentDto = CommentDto.builder().text("Хорошая вещь").build();
        Comment comment = Comment.builder().id(1L).text("Хорошая вещь").author(user1).created(minuteOfToday)
                .item(item1).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStatusAndEndBefore(eq(1L), eq(APPROVED),
                any(LocalDateTime.class))).thenReturn(List.of(booking1));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto result = itemService.createComment(1L, 1L, commentDto);
        CommentDto commentToCheck = CommentDto.builder().id(1L).text("Хорошая вещь").authorName("user1").created(minuteOfToday).build();
        assertEquals(commentToCheck, result);
    }
}
