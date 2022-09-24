package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mockMvc;

    ItemDto itemDto1 = ItemDto.builder().id(1L).name("item1")
            .description("description of item1").available(true).build();
    ItemDtoAnswerFull itemDtoAnswerFull1 = ItemDtoAnswerFull.builder().id(1L).name("item1")
            .description("description of item1").available(true).build();
    ItemDtoAnswerFull itemDtoAnswerFull2 = ItemDtoAnswerFull.builder().id(2L).name("item2")
            .description("description of item2").available(true).build();
    ItemDtoAnswer itemDtoAnswer1 = ItemDtoAnswer.builder().id(1L).name("item1").description("description of item1")
            .available(true).build();

    // getItemById
    @Test
    public void shouldReturnItemDtoAnswerFullById() throws Exception {
        Mockito.when(itemService.getItemById(1L, 1L)).thenReturn(itemDtoAnswerFull1);
        mockMvc.perform(get("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoAnswerFull1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAnswerFull1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoAnswerFull1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoAnswerFull1.getAvailable()), Boolean.class));
    }

    // getAllItemsOfUser
//    @Test
//    public void shouldReturnListOfItemDtoAnswerFull() throws Exception {
//        Mockito.when(itemService.getAllItemsOfUser(1L)).thenReturn(List.of(itemDtoAnswerFull1, itemDtoAnswerFull2));
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", "1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(itemDtoAnswerFull1.getId()), Long.class))
//                .andExpect(jsonPath("$[0].name", is(itemDtoAnswerFull1.getName()), String.class))
//                .andExpect(jsonPath("$[0].description", is(itemDtoAnswerFull1.getDescription()), String.class))
//                .andExpect(jsonPath("$[0].available", is(itemDtoAnswerFull1.getAvailable()), Boolean.class))
//                .andExpect(jsonPath("$[1].id", is(itemDtoAnswerFull2.getId()), Long.class))
//                .andExpect(jsonPath("$[1].name", is(itemDtoAnswerFull2.getName()), String.class))
//                .andExpect(jsonPath("$[1].description", is(itemDtoAnswerFull2.getDescription()), String.class))
//                .andExpect(jsonPath("$[1].available", is(itemDtoAnswerFull2.getAvailable()), Boolean.class));
//    }

    // createItem
    @Test
    public void shouldCreateItemAndReturnItemDtoAnswer() throws Exception {
        Mockito.when(itemService.createItem(1L, itemDto1)).thenReturn(itemDtoAnswer1);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoAnswer1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoAnswer1.getAvailable()), Boolean.class));
        verify(itemService, Mockito.times(1)).createItem(eq(1L), any(ItemDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenNameIsEmpty() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).name(" ")
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
                        .getMessage().contains("Название вещи не должно быть пустым")));
        verify(itemService, Mockito.times(0)).createItem(any(Long.class), any(ItemDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionIsNull() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1")
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
                        .getMessage().contains("Отсутствует описание вещи")));
        verify(itemService, Mockito.times(0)).createItem(any(Long.class), any(ItemDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenAvailableIsNull() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1")
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
                        .getMessage().contains("Отсутствует статус доступности вещи для аренды")));
        verify(itemService, Mockito.times(0)).createItem(any(Long.class), any(ItemDto.class));
    }

    // updateItem
    @Test
    public void shouldUpdateItemAndReturnItemDtoAnswer() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).name("item1")
                .description("description of item1").available(true).build();
        Mockito.when(itemService.updateItem(1L, 1L, itemDto)).thenReturn(itemDtoAnswer1);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDtoAnswer1.getName()), String.class));
        verify(itemService, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemDto.class));
    }

    @Test
    public void shouldUpdateItemWithOnlyNameAndCallItemServiceUpdateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().name("item1").build();
        Mockito.when(itemService.updateItem(1L, 1L, itemDto)).thenReturn(itemDtoAnswer1);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDtoAnswer1.getName()), String.class));
        verify(itemService, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemDto.class));
    }

    @Test
    public void shouldUpdateItemWithOnlyDescriptionAndCallItemServiceUpdateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().description("description of item1").build();
        Mockito.when(itemService.updateItem(1L, 1L, itemDto)).thenReturn(itemDtoAnswer1);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDtoAnswer1.getName()), String.class));
        verify(itemService, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemDto.class));
    }

    @Test
    public void shouldUpdateItemWithOnlyAvailableAndCallItemServiceUpdateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().available(false).build();
        Mockito.when(itemService.updateItem(1L, 1L, itemDto)).thenReturn(itemDtoAnswer1);
        mockMvc.perform(patch("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDtoAnswer1.getName()), String.class));
        verify(itemService, Mockito.times(1)).updateItem(eq(1L), eq(1L), any(ItemDto.class));
    }

    // searchForItemsByQueryText
    @Test
    public void shouldReturnCollectionOfItemDtoAnswer() throws Exception {
        Mockito.when(itemService.searchForItemsByQueryText(any(String.class))).thenReturn(List.of(itemDtoAnswer1));
        String param = "item";
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", param)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDtoAnswer1.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDtoAnswer1.getAvailable()), Boolean.class));
        Mockito.verify(itemService, Mockito.times(1)).searchForItemsByQueryText("item");
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
    public void shouldCallItemServiceCreateCommentAndReturnCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("Отличная вещь").build();
        CommentDto commentDtoAnswer = CommentDto.builder().id(1L).text("Отличная вещь").authorName("user1")
                .created(LocalDateTime.now()).build();
        when(itemService.createComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(commentDtoAnswer);
        mockMvc.perform(post("/items/{itemId}/comment", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь"))
                .andExpect(jsonPath("$.authorName").value("user1"));
        Mockito.verify(itemService, Mockito.times(1)).createComment(eq(1L), eq(1L),
                any(CommentDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenTextIsBlank() throws Exception {
        CommentDto commentDto = CommentDto.builder().text(" ").build();
        mockMvc.perform(post("/items/{itemId}/comment", "1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("Поле text не должно быть пустым")));
        Mockito.verify(itemService, Mockito.times(0)).createComment(any(Long.class), any(Long.class),
                any(CommentDto.class));
    }
}
