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

    @Test
    public void shouldCallGetUsers() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = userClient.getAllUsers();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetUserById() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = userClient.getUserById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallCreateUser() throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user1)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = userClient.createUser(user1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallUpdateUser() throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user1)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = userClient.updateUser(1L, user1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallDeleteUserById() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = userClient.deleteUserById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
