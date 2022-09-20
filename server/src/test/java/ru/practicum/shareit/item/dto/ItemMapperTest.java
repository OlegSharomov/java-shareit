package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    @InjectMocks
    private ItemMapperImpl itemMapper;
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();

    Item item1 = Item.builder()
            .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
    ItemDto itemDto1 = ItemDto.builder()
            .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
    ItemRequest itemRequest1 = ItemRequest.builder().id(1L).description("Нужна вещь").requestor(user2)
            .created(minuteOfToday).build();
    BookingDtoWithBookerId lastBooking = BookingDtoWithBookerId.builder().id(1L).bookerId(2L).build();
    BookingDtoWithBookerId nextBooking = BookingDtoWithBookerId.builder().id(2L).bookerId(3L).build();

    CommentDto commentDto1 = CommentDto.builder().id(1L).text("Отличная вещь").authorName("user1")
            .created(minuteOfToday.minusDays(2)).build();
    CommentDto commentDto2 = CommentDto.builder().id(2L).text("Работает усердно").authorName("user2")
            .created(minuteOfToday.minusDays(1)).build();
    static LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());

    // toItem
    @Test
    public void shouldReturnItemWhenWeCallToItem() {
        Item result = itemMapper.toItem(itemDto1, null);
        assertEquals(item1, result);
    }

    @Test
    public void shouldReturnItemWithRequestWhenWeCallToItem() {
        Item result = itemMapper.toItem(itemDto1, itemRequest1);
        Item itemToCheck = Item.builder().id(1L).name("item1").description("description item1").available(true)
                .owner(user1).request(itemRequest1).build();
        assertEquals(itemToCheck, result);
    }

    // toItemDtoAnswer
    @Test
    public void shouldReturnItemDtoAnswerWhenWeCallToItemDtoAnswer() {
        ItemDtoAnswer result = itemMapper.toItemDtoAnswer(item1, null);
        ItemDtoAnswer itemDtoAnswerToCheck = ItemDtoAnswer.builder().id(1L).name("item1")
                .description("description item1").available(true).build();
        assertEquals(itemDtoAnswerToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoAnswerWithRequestIdWhenWeCallToItemDtoAnswer() {
        ItemDtoAnswer result = itemMapper.toItemDtoAnswer(item1, itemRequest1);
        ItemDtoAnswer itemDtoAnswerToCheck = ItemDtoAnswer.builder().id(1L).name("item1")
                .description("description item1").available(true).requestId(1L).build();
        assertEquals(itemDtoAnswerToCheck, result);
    }

    // toItemDtoAnswerFull
    @Test
    public void shouldReturnItemDtoAnswerFullWithoutCommentsAndBookings() {
        ItemDtoAnswerFull itemDtoToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description item1").available(true).build();
        ItemDtoAnswerFull result = itemMapper.toItemDtoAnswerFull(item1, null, null, null);
        assertEquals(itemDtoToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoAnswerFullWithComments() {
        List<CommentDto> comments = List.of(commentDto1, commentDto2);
        ItemDtoAnswerFull itemDtoToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description item1").available(true).comments(comments).build();
        ItemDtoAnswerFull result = itemMapper.toItemDtoAnswerFull(item1, null, null, comments);
        assertEquals(itemDtoToCheck, result);
    }

    @Test
    public void shouldReturnItemDtoAnswerFullWithCommentsAndBookings() {
        List<CommentDto> comments = List.of(commentDto1, commentDto2);
        ItemDtoAnswerFull itemDtoToCheck = ItemDtoAnswerFull.builder().id(1L).name("item1")
                .description("description item1").available(true).comments(comments).lastBooking(lastBooking)
                .nextBooking(nextBooking).build();
        ItemDtoAnswerFull result = itemMapper.toItemDtoAnswerFull(item1, lastBooking, nextBooking, comments);
        assertEquals(itemDtoToCheck, result);
    }

    // updateItemFromDto
    @Test
    public void shouldUpdateNameWhenWeTryChangeName() {
        ItemDto itemDtoRequest = ItemDto.builder().name("item1Update").build();
        Item result = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
        itemMapper.updateItemFromDto(itemDtoRequest, result, null);
        Item itemToCheck = Item.builder()
                .id(1L).name("item1Update").description("description item1").available(true).owner(user1).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldUpdateDescriptionWhenWeTryChangeDescription() {
        ItemDto itemDtoRequest = ItemDto.builder().description("descriptionUpdate item1").build();
        Item result = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
        itemMapper.updateItemFromDto(itemDtoRequest, result, null);
        Item itemToCheck = Item.builder()
                .id(1L).name("item1").description("descriptionUpdate item1").available(true).owner(user1).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldUpdateAvailableWhenWeTryChangeAvailable() {
        ItemDto itemDtoRequest = ItemDto.builder().available(false).build();
        Item result = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
        itemMapper.updateItemFromDto(itemDtoRequest, result, null);
        Item itemToCheck = Item.builder()
                .id(1L).name("item1").description("description item1").available(false).owner(user1).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldUpdateOwnerWhenWeTryChangeOwner() {
        ItemDto itemDtoRequest = ItemDto.builder().owner(user2).build();
        Item result = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user1).build();
        itemMapper.updateItemFromDto(itemDtoRequest, result, null);
        Item itemToCheck = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user2).build();
        assertEquals(itemToCheck, result);
    }

    @Test
    public void shouldUpdateNameWhenWeTryChangeNameWithRequestId() {
        ItemDto itemDtoRequest = ItemDto.builder().name("item1Update").build();
        Item result = Item.builder()
                .id(1L).name("item1").description("description item1").available(true).owner(user1)
                .request(itemRequest1).build();
        itemMapper.updateItemFromDto(itemDtoRequest, result, null);
        Item itemToCheck = Item.builder().id(1L).name("item1Update").description("description item1")
                .available(true).owner(user1).request(itemRequest1).build();
        assertEquals(itemToCheck, result);
    }


}
