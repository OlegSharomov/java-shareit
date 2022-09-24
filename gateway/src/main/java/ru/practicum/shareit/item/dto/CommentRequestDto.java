package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    Long id;
    @NotBlank(message = "'text' is empty")
    String text;
    String authorName;
    LocalDateTime created;
}
