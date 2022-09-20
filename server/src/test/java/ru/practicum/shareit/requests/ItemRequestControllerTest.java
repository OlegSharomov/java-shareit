package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswerFull;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;

    LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    ItemDtoAnswer itemDtoAnswer1 = ItemDtoAnswer.builder().id(1L).name("item1").description("description item1")
            .available(true).requestId(1L).build();
    ItemRequestDtoAnswerFull itemRequestDtoAnswerFull1 = ItemRequestDtoAnswerFull.builder().id(1L)
            .description("Нужна вещь").created(minuteOfToday.minusDays(1)).items(List.of(itemDtoAnswer1)).build();
    ItemRequestDtoAnswerFull itemRequestDtoAnswerFull2 = ItemRequestDtoAnswerFull.builder().id(2L)
            .description("Нужна вещь2").created(minuteOfToday.minusDays(2)).items(Collections.emptyList()).build();


    // createItemRequest
    @Test
    public void shouldCallItemRequestServiceAndReturnItemRequestDtoAnswer() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Нужна вещь").build();
        ItemRequestDtoAnswer itemRequestDtoAnswer = ItemRequestDtoAnswer.builder().id(1L).description("Нужна вещь")
                .created(minuteOfToday).build();
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), eq(1L), any(LocalDateTime.class)))
                .thenReturn(itemRequestDtoAnswer);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(itemRequestService, Mockito.times(1)).createItemRequest(eq(itemRequestDto),
                eq(1L), any(LocalDateTime.class));
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsBlank() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description(" ").build();
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Поле с описанием запроса должно быть заполнено")));
        Mockito.verify(itemRequestService, Mockito.times(0)).createItemRequest(any(ItemRequestDto.class),
                eq(1L), any(LocalDateTime.class));
    }

    @Test
    public void shouldTrowExceptionWhenBodyNotExists() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
        Mockito.verify(itemRequestService, Mockito.times(0)).createItemRequest(any(ItemRequestDto.class),
                eq(1L), any(LocalDateTime.class));
    }

    // getAllItemRequestsOfUser
    @Test
    public void shouldCallItemRequestServiceGetAllItemRequestsOfUserAndReturnListOfItemRequestDtoAnswerFull() throws Exception {
        List<ItemRequestDtoAnswerFull> answer = List.of(itemRequestDtoAnswerFull1, itemRequestDtoAnswerFull2);
        when(itemRequestService.getAllItemRequestsOfUser(1L))
                .thenReturn(answer);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoAnswerFull1.getDescription()))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDtoAnswer1.getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(itemDtoAnswer1.getName()))
                .andExpect(jsonPath("$[0].items[0].requestId").value(itemDtoAnswer1.getRequestId()))
                .andExpect(jsonPath("$[0].items[0].available").value(itemDtoAnswer1.getAvailable()))
                .andExpect(jsonPath("$[1].id").value(itemRequestDtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].description").value(itemRequestDtoAnswerFull2.getDescription()))
                .andExpect(jsonPath("$[1].items", hasSize(0)));
        Mockito.verify(itemRequestService, Mockito.times(1)).getAllItemRequestsOfUser(1L);
    }

    // getAllItemRequestsByParams
    @Test
    public void shouldCallItemRequestServiceGetAllItemRequestsByParamsAndReturnListOfItemRequestDtoAnswerFull() throws Exception {
        when(itemRequestService.getAllItemRequestsByParams(1L, 0, 5)).thenReturn(List.of(itemRequestDtoAnswerFull1));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoAnswerFull1.getDescription()))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDtoAnswer1.getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(itemDtoAnswer1.getName()))
                .andExpect(jsonPath("$[0].items[0].requestId").value(itemDtoAnswer1.getRequestId()))
                .andExpect(jsonPath("$[0].items[0].available").value(itemDtoAnswer1.getAvailable()));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemRequestsByParams(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void shouldWorkWithoutFromAndSize() throws Exception {
        when(itemRequestService.getAllItemRequestsByParams(1L, null, null)).thenReturn(List.of(itemRequestDtoAnswerFull1));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoAnswerFull1.getDescription()))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDtoAnswer1.getId()))
                .andExpect(jsonPath("$[0].items[0].name").value(itemDtoAnswer1.getName()))
                .andExpect(jsonPath("$[0].items[0].requestId").value(itemDtoAnswer1.getRequestId()))
                .andExpect(jsonPath("$[0].items[0].available").value(itemDtoAnswer1.getAvailable()));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemRequestsByParams(any(Long.class), eq(null), eq(null));
    }

    @Test
    public void shouldThrowExceptionWhenFromIsNegative() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", Integer.toString(-5))
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof javax.validation.ConstraintViolationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Значение from должно быть позитивным или 0")));
        Mockito.verify(itemRequestService, Mockito.times(0))
                .getAllItemRequestsByParams(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void shouldThrowExceptionWhenSizeIsNegative() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "5")
                        .param("size", Integer.toString(-5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof javax.validation.ConstraintViolationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Значение size должно быть позитивным")));
        Mockito.verify(itemRequestService, Mockito.times(0))
                .getAllItemRequestsByParams(any(Long.class), any(Integer.class), any(Integer.class));
    }

    // getItemRequestById
    @Test
    public void shouldReturnItemRequestDtoAnswerFullById() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(itemRequestDtoAnswerFull1);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoAnswerFull1.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoAnswerFull1.getDescription()))
                .andExpect(jsonPath("$.items[0].id").value(itemDtoAnswer1.getId()))
                .andExpect(jsonPath("$.items[0].name").value(itemDtoAnswer1.getName()))
                .andExpect(jsonPath("$.items[0].requestId").value(itemDtoAnswer1.getRequestId()))
                .andExpect(jsonPath("$.items[0].available").value(itemDtoAnswer1.getAvailable()));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(any(Long.class), any(Long.class));
    }

}
