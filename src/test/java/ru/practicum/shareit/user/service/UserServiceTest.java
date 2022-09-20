package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoAnswer;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserServiceImpl userService;


    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@mail.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@mail.ru")
            .build();

    // getAllUsers
    @Test
    public void shouldReturnListDtoWhenWeGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDtoAnswer> result = userService.getAllUsers();
        assertEquals(List.of(userMapper.toUserDtoAnswer(user1), userMapper.toUserDtoAnswer(user2)), result);
    }

    @Test
    public void shouldReturnEmptyListWhenWeGetAllUsersFromEmptyRepository() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserDtoAnswer> result = userService.getAllUsers();
        assertEquals(Collections.emptyList(), result);
    }

    // getUserById
    @Test
    public void shouldReturnUserDtoWhenWeGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDtoAnswer result = userService.getUserById(1L);
        assertEquals(userMapper.toUserDtoAnswer(user1), result);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    public void shouldReturnUserWhenUserExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        User result = userService.getEntityUserByIdFromStorage(1L);
        assertEquals(user1, result);
    }

    @Test
    public void shouldThrowExceptionWhenEntityNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getEntityUserByIdFromStorage(999L));
    }

    // createUser
    @Test
    public void shouldThrowValidationExceptionWhenUserExistsInStorage() {
        when(userRepository.existsById(1L)).thenReturn(true);
        UserDto userDto = UserDto.builder().id(1L).name("user1").email("user1@mail.ru").build();
        Assertions.assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    public void shouldReturnUserWhenWeCallSaveUser() {
        UserDto userDto = UserDto.builder().id(1L).name("user1").email("user1@mail.ru").build();
        when(userRepository.save(user1)).thenReturn(user1);
        UserDtoAnswer result = userService.createUser(userDto);
        assertEquals(userMapper.toUserDtoAnswer(user1), result);
    }

    // updateUser
    @Test
    public void shouldUpdateUserNameWhenEverythingIsFine() {
        UserDto userDto = UserDto.builder().id(1L).name("user1Update").email("user1@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDtoAnswer result = userService.updateUser(1L, userDto);
        assertEquals(userMapper.toUserDtoAnswer(
                User.builder().id(1L).name("user1Update").email("user1@mail.ru").build()), result);
    }

    @Test
    public void shouldUpdateUserEmailWhenEverythingIsFine() {
        UserDto userDto = UserDto.builder().id(1L).name("user1").email("user1Update@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDtoAnswer result = userService.updateUser(1L, userDto);
        assertEquals(userMapper.toUserDtoAnswer(
                User.builder().id(1L).name("user1").email("user1Update@mail.ru").build()), result);
    }

    @Test
    public void shouldUpdateUserNameAndEmailWhenEverythingIsFine() {
        UserDto userDto = UserDto.builder().id(1L).name("user1Update").email("user1Update@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDtoAnswer result = userService.updateUser(1L, userDto);
        assertEquals(userMapper.toUserDtoAnswer(
                User.builder().id(1L).name("user1Update").email("user1Update@mail.ru").build()), result);
    }

    @Test
    public void shouldNotUpdateUserIdWhenWeTryToChangeId() {
        UserDto userDto = UserDto.builder().id(2L).name("user1Update").email("user1Update@mail.ru").build();
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals("Нельзя изменять id пользователя", re.getMessage());
    }

    @Test
    public void shouldNotUpdateUserNameWhenWeTryToChangeEmptyName() {
        UserDto userDto = UserDto.builder().id(1L).name(" ").email("user1Update@mail.ru").build();
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals("Поле name должно быть заполнено", re.getMessage());
    }

    @Test
    public void shouldNotUpdateUserEmailWhenWeTryToChangeEmptyEmail() {
        UserDto userDto = UserDto.builder().id(1L).name("user1Update").email(" ").build();
        RuntimeException re = Assertions.assertThrows(ValidationException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals("Поле email должно быть заполнено", re.getMessage());
    }

    @Test
    public void shouldNotUpdateUserNameAndEmailWhenUserNotExists() {
        UserDto userDto = UserDto.builder().id(1L).name("user1Update").email("user1Update@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, userDto));
        assertEquals("Пользователь с переданным id не найден", re.getMessage());
    }

    // deleteUserById
    @Test
    public void shouldCallDeleteByIdFromRepositoryWhenWeCallDeleteUserById() {
        userService.deleteUserById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    // isUserExists
    @Test
    public void shouldCallExistsByIdFromRepositoryAndReturnTrueWhenWeCallIsUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        boolean answer = userService.isUserExists(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Assertions.assertTrue(answer);
    }

    @Test
    public void shouldCallExistsByIdFromRepositoryAndReturnFalseWhenWeCallIsUserExists() {
        when(userRepository.existsById(1L)).thenReturn(false);
        boolean answer = userService.isUserExists(1L);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Assertions.assertFalse(answer);
    }

}
