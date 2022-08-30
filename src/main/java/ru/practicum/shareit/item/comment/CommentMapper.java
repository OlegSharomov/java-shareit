package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "authorName", source = "user.name")
    CommentDto toCommentDto(Comment comment, User user);
}
