package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(message = "'description' is empty")
    private String description;
    private LocalDateTime created;
}
