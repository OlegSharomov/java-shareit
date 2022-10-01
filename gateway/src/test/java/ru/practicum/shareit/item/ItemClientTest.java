package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReqDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(ItemClient.class)
public class ItemClientTest {
    @Autowired
    ItemClient itemClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper mapper;

    ItemReqDto item1 = ItemReqDto.builder().id(1L).name("item1")
            .description("description of item1").available(true).build();

    @AfterEach
    void checkCallParameters() {
        this.mockServer.verify();
    }


    private void expectMockServer(String addUrl, HttpMethod httpMethod) {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/items/" + addUrl))
                .andExpect(method(httpMethod))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                );
    }

    private void expectMockServerWithBody(String addUrl, HttpMethod httpMethod, ItemReqDto itemReqDto) throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/items/" + addUrl))
                .andExpect(method(httpMethod))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemReqDto)))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                );
    }

    @Test
    public void shouldCallGetItemById() {
        expectMockServer("1", GET);
        ResponseEntity<Object> result = itemClient.getItemById(1L, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetAllItemsOfUser() {
        expectMockServer("", GET);
        ResponseEntity<Object> result = itemClient.getAllItemsOfUser(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallCreateItem() throws JsonProcessingException {
        expectMockServerWithBody("", POST, item1);
        ResponseEntity<Object> result = itemClient.createItem(1L, item1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallUpdateItem() throws JsonProcessingException {
        expectMockServerWithBody("1", PATCH, item1);
        ResponseEntity<Object> result = itemClient.updateItem(1L, 1L, item1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallSearchForItemsByQueryText() {
        expectMockServer("search?text=text", GET);
        ResponseEntity<Object> result = itemClient.searchForItemsByQueryText(1L, "text");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallCreateComment() {
        CommentRequestDto comment1 = CommentRequestDto.builder().text("Отличная вещь").build();
        expectMockServer("1/comment", POST);
        ResponseEntity<Object> result = itemClient.createComment(1L, 1L, comment1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
