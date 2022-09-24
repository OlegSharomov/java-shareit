package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.requests.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestClient requestClient;
    @Autowired
    private MockMvc mockMvc;

    ResponseEntity<Object> resp = new ResponseEntity<>(HttpStatus.OK);

    // createItemRequest
    @Test
    public void shouldCallItemRequestServiceAndReturnItemRequestDtoAnswer() throws Exception {
        RequestDto itemRequestDto = RequestDto.builder().description("Нужна вещь").build();
        when(requestClient.createItemRequest(any(RequestDto.class), eq(1L))).thenReturn(resp);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestClient, Mockito.times(1)).createItemRequest(any(RequestDto.class), eq(1L));
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsBlank() throws Exception {
        RequestDto itemRequestDto = RequestDto.builder().description(" ").build();
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("'description' is empty")));
        Mockito.verify(requestClient, Mockito.times(0)).createItemRequest(any(RequestDto.class),
                eq(1L));
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
        Mockito.verify(requestClient, Mockito.times(0)).createItemRequest(any(RequestDto.class),
                eq(1L));
    }

    // getAllItemRequestsOfUser
    @Test
    public void shouldCallItemRequestServiceGetAllItemRequestsOfUserAndReturnListOfItemRequestDtoAnswerFull() throws Exception {
        when(requestClient.getAllItemRequestsOfUser(1L))
                .thenReturn(resp);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(requestClient, Mockito.times(1)).getAllItemRequestsOfUser(1L);
    }

    // getAllItemRequestsByParams
    @Test
    public void shouldCallItemRequestServiceGetAllItemRequestsByParamsAndReturnListOfItemRequestDtoAnswerFull() throws Exception {
        when(requestClient.getAllItemRequestsByParams(1L, 0, 5)).thenReturn(resp);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(requestClient, Mockito.times(1))
                .getAllItemRequestsByParams(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void shouldWorkWithoutFromAndSize() throws Exception {
        when(requestClient.getAllItemRequestsByParams(1L, 0, 10)).thenReturn(resp);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(requestClient, Mockito.times(1))
                .getAllItemRequestsByParams(any(Long.class), eq(0), eq(10));
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
                        .contains("'from' must be positive or zero")));
        Mockito.verify(requestClient, Mockito.times(0))
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
                        .contains("'size' must be positive")));
        Mockito.verify(requestClient, Mockito.times(0))
                .getAllItemRequestsByParams(any(Long.class), any(Integer.class), any(Integer.class));
    }

    // getItemRequestById
    @Test
    public void shouldReturnItemRequestDtoAnswerFullById() throws Exception {
        when(requestClient.getItemRequestById(1L, 1L)).thenReturn(resp);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(requestClient, Mockito.times(1))
                .getItemRequestById(any(Long.class), any(Long.class));
    }

}
