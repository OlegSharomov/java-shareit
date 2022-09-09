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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    // getEntityItemByIdFromStorage
    @Test
    public void shouldReturnEntityWhenEntityExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Item item = itemService.getEntityItemByIdFromStorage(1L);
        assertEquals(item, item1);
    }

    @Test
    public void shouldThrowExceptionWhenEntityNotExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getEntityItemByIdFromStorage(1L));
        assertEquals(re.getMessage(), "Вещь с переданным id = 1 отсутствует в хранилище");
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
        assertEquals(re.getMessage(), "Пользователь с переданным id = 999 не найден");
    }

    @Test
    public void shouldThrowExceptionWhenItemNotExists() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 999L));
        assertEquals(re.getMessage(), "Вещь с переданным id = 999 отсутствует в хранилище");
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
    @Test
    public void shouldReturnEmptyListWhenOwnerHasNoItems() {
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());
        List<Item> result = itemService.getAllEntityItemsOfUserFromStorage(1L);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void shouldReturnListOfItemsWhenOwnerHasNoItems() {
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        List<Item> result = itemService.getAllEntityItemsOfUserFromStorage(1L);
        assertEquals(List.of(item1, item2), result);
    }

    // getAllItemsOfUser
    @Test
    public void shouldThrowExceptionWhenWeCallGetAllItemsOfUser() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getAllItemsOfUser(999L));
        assertEquals(re.getMessage(), "Пользователь с переданным id = 999 не найден");
    }

    @Test
    public void shouldReturnEmptyListDtoWhenOwnerHasNoItems() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());
        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void shouldReturnListDtoWithCommentsWhenOwnerHasItems() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
        ItemDtoAnswerFull itemToCheck1 = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(Collections.emptyList())
                .lastBooking(null).nextBooking(null).build();
        ItemDtoAnswerFull itemToCheck2 = ItemDtoAnswerFull.builder().id(2L).name("item2")
                .description("description of item2").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(null).nextBooking(null).build();
        assertEquals(List.of(itemToCheck1, itemToCheck2), result);
    }

    @Test
    public void shouldReturnListDtoWithCommentAndBookingsWhenOwnerHasItems() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(1L), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(eq(2L), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findByItemIdAndStartIsAfterOrderByStart(eq(2L), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        List<ItemDtoAnswerFull> result = itemService.getAllItemsOfUser(1L);
        ItemDtoAnswerFull itemToCheck1 = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description of item1").available(true).comments(Collections.emptyList())
                .lastBooking(null).nextBooking(null).build();
        ItemDtoAnswerFull itemToCheck2 = ItemDtoAnswerFull.builder().id(2L).name("item2")
                .description("description of item2").available(true).comments(List.of(commentMapper.toCommentDto(comment1, user2),
                        commentMapper.toCommentDto(comment2, user3)))
                .lastBooking(bookingMapper.toBookingDtoWithBookerId(lastBooking))
                .nextBooking(bookingMapper.toBookingDtoWithBookerId(nextBooking)).build();
        assertEquals(List.of(itemToCheck1, itemToCheck2), result);
    }

    // createItem
    @Test
    public void shouldThrowExceptionWhenWeCallCreateItemAndUserNotExists() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(999L, ItemDto.builder()
                        .name("item1").description("description of item1").available(true).build()));
        assertEquals(re.getMessage(), "Пользователь с переданным id = 999 не найден");
    }

    @Test
    public void shouldThrowExceptionWhenItemIsAlreadyExist() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, ItemDto.builder()
                        .id(1L).name("item1Update").description("descriptionUpdate of item1").available(true).build()));
        assertEquals(re.getMessage(), "Данные вещи можно изменять только через метод PATCH");
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
        assertEquals(re.getMessage(), "Переданный id запроса вещи не найден");
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
        assertEquals(re.getMessage(), "Пользователь, создавший запрос не может предлагать для него вещи");
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
    public void shouldThrowExceptionWhenWeTryChangeId() {
        ItemDto itemDto = ItemDto.builder().id(99L).build();
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));
        assertEquals(re.getMessage(), "Id вещи изменять нельзя");
    }

    @Test
    public void shouldThrowExceptionWhenWeTryChangeEmptyName() {
        ItemDto itemDto = ItemDto.builder().name(" ").owner(user1).build();
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));
        assertEquals(re.getMessage(), "Название вещи не должно быть пустым");
    }

    @Test
    public void shouldThrowExceptionWhenWeTryUpdateItemAndUserNotExists() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").build();
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(999L, 1L, itemDto));
        assertEquals(re.getMessage(), "Пользователь с переданным id = 999 не найден");
    }

    @Test
    public void shouldThrowExceptionWhenWeTryUpdateItemWithItemRequestIdAndItemRequestNotExists() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").requestId(1L).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRequestRepository.existsById(1L)).thenReturn(false);
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));
        assertEquals(re.getMessage(), "Переданный id = 1 запроса вещи не найден");
    }

    @Test
    public void shouldThrowExceptionWhenItemNotFound() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").owner(user1).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 999L, itemDto));
        assertEquals(re.getMessage(), "Вещь с переданным id = 999 не найдена");
    }

    @Test
    public void shouldThrowExceptionWhenUserAndOwnerDifferent() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").owner(user2).build();
        when(userService.isUserExists(2L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        RuntimeException re = Assertions.assertThrows(OwnerVerificationException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));
        assertEquals(re.getMessage(), "Доступ к редактированию ограничен. " +
                "Редактировать вещь может только её владелец.");
    }

    @Test
    public void shouldReturnItemDtoWithDifferentNameWhenWeUpdateName() {
        ItemDto itemDto = ItemDto.builder().name("item1Update").owner(user2).build();
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
        ItemDto itemDto = ItemDto.builder().description("descriptionUpdate of item1").owner(user2).build();
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
        ItemDto itemDto = ItemDto.builder().available(false).owner(user2).build();
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ItemDtoAnswer result = itemService.updateItem(1L, 1L, itemDto);
        ItemDtoAnswer itemToCheck = ItemDtoAnswer.builder().id(1L).name("item1").description("description of item1")
                .available(false).build();
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        assertEquals(itemToCheck, result);
    }



}
