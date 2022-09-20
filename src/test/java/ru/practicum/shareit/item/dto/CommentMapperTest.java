package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapperImpl commentMapper;
    static LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    CommentDto commentDto1 = CommentDto.builder().id(1L).text("Отличная вещь").authorName("user1")
            .created(minuteOfToday.minusDays(2)).build();
    Comment comment1 = Comment.builder().id(1L).text("Отличная вещь").author(user1)
            .created(minuteOfToday.minusDays(2)).build();


    @Test
    public void should() {
        CommentDto result = commentMapper.toCommentDto(comment1, user1);
        Assertions.assertEquals(commentDto1, result);
    }
}
