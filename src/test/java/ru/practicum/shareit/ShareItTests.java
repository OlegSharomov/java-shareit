package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShareItTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    ItemDto item1 = ItemDto.builder().name("Шуруповерт").description("Аккумуляторный").available(true).build();
    ItemDto item2 = ItemDto.builder().id(1L).name("Перфоратор").description("Сетевой").available(true).build();

    private String getJacksonUserWithoutId() throws JsonProcessingException {
        UserDto user = UserDto.builder()
                .name("Andre")
                .email("Andre@mail.com")
                .build();
        return mapper.writeValueAsString(user);
    }

    private String getJacksonUserId2() throws JsonProcessingException {
        UserDto user = UserDto.builder()
                .id(2L)
                .name("Nicolay")
                .email("Nicolay@mail.com")
                .build();
        return mapper.writeValueAsString(user);
    }

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    public void shouldCreateUserWithWrongId() throws Exception {
        mockMvc.perform(post("/users").content(getJacksonUserId2())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Nicolay"))
                .andExpect(jsonPath("$.email").value("Nicolay@mail.com"));
    }

    @Test
    @Order(2)
    public void shouldNotCreateUserWithDuplicateEmail() throws Exception {
        mockMvc.perform(post("/users").content(getJacksonUserId2())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    public void shouldNotCreateUserWithoutEmail() throws Exception {
        UserDto user = UserDto.builder()
                .name("Andre")
                .build();
        mockMvc.perform(post("/users").content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void shouldNotCreateUserWithFailEmail() throws Exception {
        UserDto user = UserDto.builder()
                .name("Andre")
                .build();
        mockMvc.perform(post("/users").content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void shouldNotCreateUserWithoutName() throws Exception {
        UserDto user = UserDto.builder()
                .email("Vasiliy")
                .build();
        mockMvc.perform(post("/users").content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void shouldCreateUserWithoutId() throws Exception {
        mockMvc.perform(post("/users").content(getJacksonUserWithoutId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.name").value("Andre"))
                .andExpect(jsonPath("$.email").value("Andre@mail.com"));
    }

    @Test
    @Order(7)
    public void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Nicolay"))
                .andExpect(jsonPath("$[0].email").value("Nicolay@mail.com"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Andre"))
                .andExpect(jsonPath("$[1].email").value("Andre@mail.com"));
    }

    @Test
    @Order(8)
    public void shouldUpdateUser() throws Exception {
        UserDto userDto = UserDto.builder().name("NicolayUpdate").email("NicolayUpdate@mail.com").build();
        mockMvc.perform(patch("/users/{userId}", 1).content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NicolayUpdate"))
                .andExpect(jsonPath("$.email").value("NicolayUpdate@mail.com"));
    }

    @Test
    @Order(9)
    public void shouldNotUpdateUserWithWrongId() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"id\":2}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/users/{userId}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NicolayUpdate"))
                .andExpect(jsonPath("$.email").value("NicolayUpdate@mail.com"));
    }

    @Test
    @Order(10)
    public void shouldChangeName() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"name\":\"NiCola\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NiCola"))
                .andExpect(jsonPath("$.email").value("NicolayUpdate@mail.com"));
    }

    @Test
    @Order(11)
    public void shouldChangeEmail() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"email\":\"NiColaUpdate@mail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NiCola"))
                .andExpect(jsonPath("$.email").value("NiColaUpdate@mail.com"));
    }

    @Test
    @Order(12)
    public void shouldNotChangeDuplicateEmail() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"email\":\"Andre@mail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @Order(13)
    public void shouldNotChangeEmptyName() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"name\":\" \"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    public void shouldNotChangeEmptyEmail() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 1).content("{\"email\":\" \"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    public void shouldNotRemoveNonexistentUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(16)
    public void shouldCreateItem1() throws Exception {
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value("Шуруповерт"))
                .andExpect(jsonPath("description").value("Аккумуляторный"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @Order(17)
    public void shouldNotCreateItemWithWrongUserId() throws Exception {
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(item2))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(18)
    public void shouldNotCreateItemWithEmptyName() throws Exception {
        ItemDto itemDto = ItemDto.builder().name(" ").description("Описание").available(true).build();
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(19)
    public void shouldNotCreateItemWWithoutDescription() throws Exception {
        ItemDto itemDto = ItemDto.builder().name("Имя").available(true).build();
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(20)
    public void shouldNotCreateItemWithoutAvailable() throws Exception {
        ItemDto itemDto = ItemDto.builder().name("Строительный фен").description("950 С").build();
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(21)
    public void shouldCreateItem2WithWrongId() throws Exception {
        mockMvc.perform(post("/items").content(mapper.writeValueAsString(item2))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(jsonPath("id").value("2"))
                .andExpect(jsonPath("name").value("Перфоратор"))
                .andExpect(jsonPath("description").value("Сетевой"))
                .andExpect(jsonPath("available").value(true));
    }

    @Test
    @Order(22)
    public void shouldGetAllItems() throws Exception {
        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Шуруповерт"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторный"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Перфоратор"))
                .andExpect(jsonPath("$[1].description").value("Сетевой"))
                .andExpect(jsonPath("$[1].available").value(true));
    }

    @Test
    @Order(23)
    public void shouldUpdateNameOfItem1() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"name\":\"Шуруповерт с битами\"}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Шуруповерт с битами"))
                .andExpect(jsonPath("$.description").value("Аккумуляторный"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(24)
    public void shouldUpdateDescriptionOfItem1() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"description\":\"Аккумуляторный 18V\"}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Шуруповерт с битами"))
                .andExpect(jsonPath("$.description").value("Аккумуляторный 18V"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(25)
    public void shouldUpdateAvailableOfItem1() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"available\":false}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Шуруповерт с битами"))
                .andExpect(jsonPath("$.description").value("Аккумуляторный 18V"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @Order(26)
    public void shouldNotUpdateItem1WithWrongId() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"id\":99}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/items/{itemId}", 1).header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Шуруповерт с битами"))
                .andExpect(jsonPath("$.description").value("Аккумуляторный 18V"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @Order(27)
    public void shouldNotUpdateItem1WithWrongName() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"name\":\" \"}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(28)
    public void shouldNotUpdateItem1WithWrongOwner() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"name\":\"Шурик\"}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "2"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(29)
    public void shouldNotUpdateItem1WithNonExistsItemId() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 99).content("{\"name\":\"Шурик\"}")
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(30)
    public void shouldNotFindItemWithFalseAvailable() throws Exception {
        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", "2")
                        .param("text", "аККум").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    @Order(31)
    public void shouldFindItemByDescription() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1).content("{\"available\":true}")
                        .header("X-Sharer-User-Id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", "2")
                        .param("text", "аККум").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Шуруповерт с битами"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторный 18V"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @Order(32)
    public void shouldFindItemByName() throws Exception {
        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", "2")
                        .param("text", "перФ").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].name").value("Перфоратор"))
                .andExpect(jsonPath("$[0].description").value("Сетевой"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
}
