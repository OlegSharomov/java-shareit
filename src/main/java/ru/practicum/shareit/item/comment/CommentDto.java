package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDto {
    Long id;
    @NotBlank(message = "Поле text не должно быть пустым")
    String text;
    String authorName;
    LocalDateTime created;
}
