package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
//    @Mapping(source = "comment.author.name", target = "authorName")
    public CommentDto toCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
