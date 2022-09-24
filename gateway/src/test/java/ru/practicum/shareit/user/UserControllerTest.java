package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserClient userClient;
    @Autowired
    private MockMvc mockMvc;

    ResponseEntity<Object> resp = new ResponseEntity<>(HttpStatus.OK);
    UserRequestDto userDto1 = new UserRequestDto(1L, "User1", "user1@mail.ru");

    // getAllUsers
    @Test
    public void shouldReturnListOfUserDtoAnswer() throws Exception {
        when(userClient.getAllUsers()).thenReturn(resp);
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).getAllUsers();
    }

    // getUserById
    @Test
    public void shouldReturnUserDtoAnswer() throws Exception {
        when(userClient.getUserById(1L)).thenReturn(resp);
        mockMvc.perform(get("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).getUserById(1L);
    }

    // createUser
    @Test
    public void shouldReturnUserDtoAfterCreated() throws Exception {
        when(userClient.createUser(any(UserRequestDto.class))).thenReturn(resp);
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenNameIsEmpty() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().name(" ").email("user1@mail.ru").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The user name is blank")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenMailIsNull() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().name("user1").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The user's email is missing")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenMailIsIncorrect() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().name("user1").email("fffffffff").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("Email is not correct")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    // updateUser
    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeAllFieldsOfUser() throws Exception {
        when(userClient.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(resp);
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeFieldName() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().name("userUpdate").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeFieldEmail() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().email("userUpdate@mail.ru").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    public void shouldThrowValidationExceptionWhenMailEmpty() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().email(" ").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The email field must be filled in")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void shouldThrowValidationExceptionWhenNameEmpty() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().name(" ").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("The name field must be filled in")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void shouldThrowValidationExceptionWhenWeTryChangeId() throws Exception {
        UserRequestDto userDto = UserRequestDto.builder().id(2L).build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("You cannot change the user ID")));
        Mockito.verify(userClient, Mockito.times(0)).createUser(any(UserRequestDto.class));
    }

    @Test
    public void should() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).deleteUserById(1L);
    }
}
