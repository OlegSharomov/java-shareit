package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(UserClient.class)
public class UserClientTest {
    @Autowired
    UserClient userClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper mapper;

    UserRequestDto user1 = new UserRequestDto(1L, "User1", "user1@mail.ru");

    @AfterEach
    void checkCallParameters() {
        this.mockServer.verify();
    }

    private void expectMockServer(String addUrl, HttpMethod httpMethod) {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users" + addUrl))
                .andExpect(method(httpMethod))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
    }

    private void expectMockServerWithBody(String addUrl, HttpMethod httpMethod, UserRequestDto userDto) throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users" + addUrl))
                .andExpect(method(httpMethod))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    public void shouldCallGetUsers() {
        expectMockServer("", GET);
        ResponseEntity<Object> result = userClient.getAllUsers();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetUserById() {
        expectMockServer("/1", GET);
        ResponseEntity<Object> result = userClient.getUserById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallCreateUser() throws JsonProcessingException {
        expectMockServerWithBody("", POST, user1);
        ResponseEntity<Object> result = userClient.createUser(user1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallUpdateUser() throws JsonProcessingException {
        expectMockServerWithBody("/1", PATCH, user1);
        ResponseEntity<Object> result = userClient.updateUser(1L, user1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallDeleteUserById() {
        expectMockServer("/1", DELETE);
        ResponseEntity<Object> result = userClient.deleteUserById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
