package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    public void shouldReturnUserWhenWeMapUser() {
        UserDto userDto = UserDto.builder().id(1L).name("user1").email("user1@mail.ru").build();
        User user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        User result = userMapper.toUser(userDto);
        Assertions.assertEquals(user, result);
    }

    @Test
    public void shouldReturnNullWhenWeMapNullUser() {
        User result = userMapper.toUser(null);
        Assertions.assertNull(result);
    }

    @Test
    public void shouldReturnUserDtoAnswerWhenWeMapUser() {
        UserDtoAnswer userDtoAnswer = UserDtoAnswer.builder().id(1L).name("user1").email("user1@mail.ru").build();
        User user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        UserDtoAnswer result = userMapper.toUserDtoAnswer(user);
        Assertions.assertEquals(userDtoAnswer, result);
    }

    @Test
    public void shouldReturnNullWhenWeMapNullUserDtoAnswer() {
        UserDtoAnswer result = userMapper.toUserDtoAnswer(null);
        Assertions.assertNull(result);
    }

    @Test
    public void shouldChangeNameWhenWeUpdateOnlyName() {
        UserDto userDtoFromRequest = UserDto.builder().name("user1Update").build();
        User result = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        userMapper.updateUserFromDto(userDtoFromRequest, result);
        User userForCheck = User.builder().id(1L).name("user1Update").email("user1@mail.ru").build();
        Assertions.assertEquals(userForCheck, result);
    }

    @Test
    public void shouldChangeEmailWhenWeUpdateOnlyEmail() {
        UserDto userDtoFromRequest = UserDto.builder().email("user1Update@mail.ru").build();
        User result = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        userMapper.updateUserFromDto(userDtoFromRequest, result);
        User userForCheck = User.builder().id(1L).name("user1").email("user1Update@mail.ru").build();
        Assertions.assertEquals(userForCheck, result);
    }


}
