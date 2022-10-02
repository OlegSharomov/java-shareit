package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReqDto;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemClient itemClient;
    @Autowired
    private MockMvc mockMvc;

    ResponseEntity<Object> resp = new ResponseEntity<>(HttpStatus.OK);
    ItemReqDto itemDto1 = ItemReqDto.builder().id(1L).name("item1")
            .description("description of item1").available(true).build();

    // getItemById
    @Test
    public void shouldCallGetItemByIdAndReturnAnswer() throws Exception {
        Mockito.when(itemClient.getItemById(1L, 1L)).thenReturn(resp);
        mockMvc.perform(get("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).getItemById(eq(1L), eq(1L));
    }

    @Test
    public void shouldThrowConstraintViolationExceptionWhenIdNegative() throws Exception {
        mockMvc.perform(get("/items/{itemId}", "-1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("id must be positive")));
        verify(itemClient, Mockito.times(0)).getItemById(any(Long.class), any(Long.class));
    }

    //     getAllItemsOfUser
    @Test
    public void shouldCallGetAllItemsOfUserAndReturnAnswer() throws Exception {
        Mockito.when(itemClient.getAllItemsOfUser(1L)).thenReturn(resp);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).getAllItemsOfUser(eq(1L));

    }

    // createItem
    @Test
    public void shouldCallCreateItemAndReturnAnswer() throws Exception {
        Mockito.when(itemClient.createItem(1L, itemDto1)).thenReturn(resp);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).createItem(eq(1L), any(ItemReqDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenNameIsEmpty() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().id(1L).name(" ")
                .description("description of item1").available(true).build();
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The name of item is empty")));
        verify(itemClient, Mockito.times(0)).createItem(any(Long.class), any(ItemReqDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsNull() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().id(1L).name("item1")
                .description(null).available(true).build();
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The description of item is null")));
        verify(itemClient, Mockito.times(0)).createItem(any(Long.class), any(ItemReqDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenAvailableIsNull() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().id(1L).name("item1")
                .description("description of item1").available(null).build();
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The available of item is null")));
        verify(itemClient, Mockito.times(0)).createItem(any(Long.class), any(ItemReqDto.class));
    }

    // updateItem
    @Test
    public void shouldUpdateItemAndReturnAnswer() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().id(1L).name("item1")
                .description("description of item1").available(true).build();
        Mockito.when(itemClient.updateItem(1L, 1L, itemDto)).thenReturn(resp);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemReqDto.class));
    }

    @Test
    public void shouldCallUpdateItemWithOnlyName() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().name("item1").build();
        Mockito.when(itemClient.updateItem(1L, 1L, itemDto)).thenReturn(resp);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemReqDto.class));
    }

    @Test
    public void shouldCallUpdateItemWithOnlyDescription() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().description("description of item1").build();
        Mockito.when(itemClient.updateItem(1L, 1L, itemDto)).thenReturn(resp);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemReqDto.class));
    }

    @Test
    public void shouldCallUpdateItemWithOnlyAvailable() throws Exception {
        ItemReqDto itemDto = ItemReqDto.builder().available(false).build();
        Mockito.when(itemClient.updateItem(1L, 1L, itemDto)).thenReturn(resp);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemClient, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemReqDto.class));
    }

    // searchForItemsByQueryText
    @Test
    public void shouldCallSearchForItemsByQueryText() throws Exception {
        Mockito.when(itemClient.searchForItemsByQueryText(eq(1L), any(String.class))).thenReturn(resp);
        String param = "item";
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", param)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(itemClient, Mockito.times(1)).searchForItemsByQueryText(1L, "item");
    }

    @Test
    public void shouldThrowExceptionWhenRequestWithoutParamIsWrong() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MissingServletRequestParameterException));
    }

    // createComment
    @Test
    public void shouldCallItemServiceCreateComment() throws Exception {
        CommentRequestDto commentDto = CommentRequestDto.builder().text("Отличная вещь").build();
        when(itemClient.createComment(eq(1L), eq(1L), any(CommentRequestDto.class))).thenReturn(resp);
        mockMvc.perform(post("/items/{itemId}/comment", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(itemClient, Mockito.times(1)).createComment(eq(1L), eq(1L),
                any(CommentRequestDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenTextIsBlank() throws Exception {
        CommentRequestDto commentDto = CommentRequestDto.builder().text(" ").build();
        mockMvc.perform(post("/items/{itemId}/comment", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("'text' is empty")));
        Mockito.verify(itemClient, Mockito.times(0)).createComment(any(Long.class), any(Long.class),
                any(CommentRequestDto.class));
    }
}
