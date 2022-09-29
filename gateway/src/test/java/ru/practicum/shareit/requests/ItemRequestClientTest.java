package ru.practicum.shareit.requests;

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
import ru.practicum.shareit.requests.dto.RequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(ItemRequestClient.class)
public class ItemRequestClientTest {
    @Autowired
    ItemRequestClient itemRequestClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper mapper;

    RequestDto itemRequestDto1 = RequestDto.builder().description("Нужна вещь").build();

    @AfterEach
    void checkCallParameters() {
        this.mockServer.verify();
    }

    @Test
    public void shouldCallCreateItemRequest() throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto1)))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = itemRequestClient.createItemRequest(itemRequestDto1, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetAllItemRequestsOfUser() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = itemRequestClient.getAllItemRequestsOfUser(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetAllItemRequestsByParams() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/requests/all?from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = itemRequestClient.getAllItemRequestsByParams(1L, 0, 10);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetItemRequestById() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/requests/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = itemRequestClient.getItemRequestById(1L, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
