package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {
    @InjectMocks
    ItemRequestMapperImpl itemRequestMapper;

    LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).description("Нужна вещь").requestor(user1)
            .created(minuteOfToday).build();
    ItemRequest request1 = ItemRequest.builder().id(1L).description("Нужна вещь").requestor(user1)
            .created(minuteOfToday).build();
    ItemDtoAnswer itemDto1 = ItemDtoAnswer.builder().id(1L).name("item1").description("description item1").available(true)
            .requestId(1L).build();
    ItemDtoAnswer itemDto2 = ItemDtoAnswer.builder().id(2L).name("item2").description("description item2").available(true)
            .requestId(1L).build();
    List<ItemDtoAnswer> items = List.of(itemDto1, itemDto2);

    // toItemRequest
    @Test
    public void shouldReturnItemRequest() {
        ItemRequest requestToCheck = ItemRequest.builder().id(1L).description("Нужна вещь").requestor(user1)
                .created(minuteOfToday).build();
        ItemRequest result = itemRequestMapper.toItemRequest(requestDto, user1, minuteOfToday);
        assertEquals(requestToCheck, result);
    }

    // toItemRequestDtoAnswer
    @Test
    public void shouldReturnItemRequestDtoAnswer() {

        ItemRequestDtoAnswer requestDtoToCheck = ItemRequestDtoAnswer.builder().id(1L).description("Нужна вещь")
                .created(minuteOfToday).build();
        ItemRequestDtoAnswer result = itemRequestMapper.toItemRequestDtoAnswer(request1);
        assertEquals(requestDtoToCheck, result);
    }

    // toItemRequestDtoAnswerFull
    @Test
    public void shouldReturnItemRequestDtoAnswerFullWithoutItems() {
        ItemRequestDtoAnswerFull requestDtoToCheck = ItemRequestDtoAnswerFull.builder().id(1L).description("Нужна вещь")
                .created(minuteOfToday).items(null).build();
        ItemRequestDtoAnswerFull result = itemRequestMapper.toItemRequestDtoAnswerFull(request1, null);
        assertEquals(requestDtoToCheck, result);
    }

    @Test
    public void shouldReturnItemRequestDtoAnswerFullWithItems() {
        ItemRequestDtoAnswerFull requestDtoToCheck = ItemRequestDtoAnswerFull.builder().id(1L).description("Нужна вещь")
                .created(minuteOfToday).items(items).build();
        ItemRequestDtoAnswerFull result = itemRequestMapper.toItemRequestDtoAnswerFull(request1, items);
        assertEquals(requestDtoToCheck, result);
    }
}
