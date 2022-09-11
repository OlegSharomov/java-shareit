package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswerFull;
import ru.practicum.shareit.requests.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestsRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Spy
    private ItemRequestMapperImpl itemRequestMapper;
    @Spy
    private ItemMapperImpl itemMapper;

    static LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
    ItemRequest itemRequest1 = ItemRequest.builder().id(1L).description("Нужна вещь1").requestor(user1)
            .created(minuteOfToday.minusDays(2)).build();
    ItemRequest itemRequest2 = ItemRequest.builder().id(2L).description("Нужна вещь2").requestor(user1)
            .created(minuteOfToday.minusDays(3)).build();
    ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
            .id(1L).description("Нужна вещь1").requestor(user1).created(minuteOfToday.minusDays(2)).build();
    Item item1 = Item.builder().id(1L).name("item1").description("description item1").available(true)
            .owner(user2).request(itemRequest2).build();
    ItemDtoAnswer itemDtoAnswer1 = ItemDtoAnswer.builder().id(1L).name("item1").description("description item1")
            .available(true).requestId(2L).build();
    ItemRequestDtoAnswerFull itemRequestDtoAnswerFull1 = ItemRequestDtoAnswerFull.builder().id(1L)
            .description("Нужна вещь1").created(minuteOfToday.minusDays(2)).items(Collections.emptyList()).build();
    ItemRequestDtoAnswerFull itemRequestDtoAnswerFull2 = ItemRequestDtoAnswerFull.builder().id(2L)
            .description("Нужна вещь2").created(minuteOfToday.minusDays(3)).items(List.of(itemDtoAnswer1)).build();

    // createItemRequest
    @Test
    public void shouldCreateAndReturnItemRequestDtoAnswerWhenWeCallCreateItemRequest() {
        when(userService.getEntityUserByIdFromStorage(1L)).thenReturn(user1);
        ItemRequestDtoAnswer itemRequestToCheck = ItemRequestDtoAnswer.builder().id(1L).description("Нужна вещь1")
                .created(minuteOfToday.minusDays(2)).build();
        ItemRequestDtoAnswer result = itemRequestService.createItemRequest(itemRequestDto1, 1L,
                minuteOfToday.minusDays(2));
        Mockito.verify(itemRequestsRepository, Mockito.times(1)).save(any(ItemRequest.class));
        assertEquals(itemRequestToCheck, result);
    }

    // getAllItemRequestsOfUser
    @Test
    public void shouldReturnListOfItemRequestDtoAnswerFullWhenWeCallGetAllItemRequestsOfUser() {
        when(userService.getEntityUserByIdFromStorage(1L)).thenReturn(user1);
        when(itemRequestsRepository.getItemRequestsByRequestor(user1))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequest(itemRequest2)).thenReturn(List.of(item1));
        List<ItemRequestDtoAnswerFull> listToCheck = List.of(itemRequestDtoAnswerFull1, itemRequestDtoAnswerFull2);
        List<ItemRequestDtoAnswerFull> result = itemRequestService.getAllItemRequestsOfUser(1L);
        assertEquals(listToCheck, result);
    }

    // getAllItemRequestsByParams
    @Test
    public void shouldReturnEmptyListWhenRequestWithoutParamsFromAndSize() {
        List<ItemRequestDtoAnswerFull> result = itemRequestService.getAllItemRequestsByParams(1L, null, null);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void shouldReturnListOfItemRequestDtoAnswerFullWhenWeCallGetAllItemRequestsByParams() {
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest1, itemRequest2));
        when(userService.getEntityUserByIdFromStorage(2L)).thenReturn(user2);
        when(itemRequestsRepository.findAllByRequestorNotLike(eq(user2), any(Pageable.class)))
                .thenReturn(page);
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequest(itemRequest2)).thenReturn(List.of(item1));
        List<ItemRequestDtoAnswerFull> listToCheck = List.of(itemRequestDtoAnswerFull1, itemRequestDtoAnswerFull2);
        List<ItemRequestDtoAnswerFull> result = itemRequestService.getAllItemRequestsByParams(2L, 0, 5);
        assertEquals(listToCheck, result);
    }

    // getItemRequest
    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(999L, 1L));
        assertEquals("Пользователь с переданным id = 999 не найден", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenItemRequestNotExists() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRequestsRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 999L));
        assertEquals("Запрос с id = 999 не найден", re.getMessage());
    }

    @Test
    public void shouldReturnItemRequestDtoAnswerFullWhenWeCallGetItemRequest() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(itemRequestsRepository.findById(2L)).thenReturn(Optional.of(itemRequest2));
        when(itemRepository.findAllByRequest(itemRequest2)).thenReturn(List.of(item1));
        ItemRequestDtoAnswerFull result = itemRequestService.getItemRequest(1L, 2L);
        assertEquals(itemRequestDtoAnswerFull2, result);
    }
}
