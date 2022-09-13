package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mockMvc;

    UserDto userDto1 = UserDto.builder().id(1L).name("user1").email("user1@mail.ru").build();
    UserDtoAnswer userDtoAnswer1 = UserDtoAnswer.builder().id(1L).name("user1").email("user1@mail.ru").build();
    UserDtoAnswer userDtoAnswer2 = UserDtoAnswer.builder().id(2L).name("user2").email("user2@mail.ru").build();

    // getAllUsers
    @Test
    public void shouldReturnListOfUserDtoAnswer() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDtoAnswer1, userDtoAnswer2));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userDtoAnswer1.getEmail()), String.class))
                .andExpect(jsonPath("$[1].id", is(userDtoAnswer2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDtoAnswer2.getName()), String.class))
                .andExpect(jsonPath("$[1].email", is(userDtoAnswer2.getEmail()), String.class));
    }

    // getUserById
    @Test
    public void shouldReturnUserDtoAnswer() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDtoAnswer1);
        mockMvc.perform(get("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoAnswer1.getEmail()), String.class));
    }

    // createUser
    @Test
    public void shouldReturnUserDtoAfterCreated() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDtoAnswer1);
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoAnswer1.getEmail()), String.class));
        Mockito.verify(userService, Mockito.times(1)).createUser(any(UserDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenNameIsEmpty() throws Exception {
        UserDto userDto = UserDto.builder().name(" ").email("user1@mail.ru").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("Имя пользователя отсутствует")));
        Mockito.verify(userService, Mockito.times(0)).createUser(any(UserDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenMailIsNull() throws Exception {
        UserDto userDto = UserDto.builder().name("user1").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("Email пользователя отсутствует")));
        Mockito.verify(userService, Mockito.times(0)).createUser(any(UserDto.class));
    }

    @Test
    public void shouldReturnStatus400WhenMailIsIncorrect() throws Exception {
        UserDto userDto = UserDto.builder().name("user1").email("fffffffff").build();
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("Email не проходит проверку")));
        Mockito.verify(userService, Mockito.times(0)).createUser(any(UserDto.class));
    }



    // updateUser
    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeAllFieldsOfUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(userDtoAnswer1);
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoAnswer1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoAnswer1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoAnswer1.getEmail()), String.class));
        Mockito.verify(userService, Mockito.times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeFieldName() throws Exception {
        UserDto userDto = UserDto.builder().name("userUpdate").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).updateUser(eq(1L), any(UserDto.class));
    }


    @Test
    public void shouldReturnUpdatedUserDtoAnswerWhenWeChangeFieldEmail() throws Exception {
        UserDto userDto = UserDto.builder().email("userUpdateEmail").build();
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).updateUser(eq(1L), any(UserDto.class));

    }
}
