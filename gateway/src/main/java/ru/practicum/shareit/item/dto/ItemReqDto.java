package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemReqDto {
    private Long id;
    @NotBlank(message = "The name of item is empty")
    private String name;
    @NotNull(message = "The description of item is null")
    private String description;
    @NotNull(message = "The available of item is null")
    private Boolean available;
    private Long requestId;
}
