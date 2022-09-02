package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswerFull;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    /*POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса,
    где пользователь описывает, какая именно вещь ему нужна.*/
    @PostMapping
    public ItemRequestDtoAnswer createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST/requests от пользователя id = {} с телом запроса: {}", userId, itemRequestDto);
        LocalDateTime createDate = LocalDateTime.now();
        return itemRequestService.createItemRequest(itemRequestDto, userId, createDate);
    }

    /*GET /requests — получить список своих запросов вместе с данными об ответах на них.
    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи,
    название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию
    о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых к более старым.*/
    @GetMapping
    public List<ItemRequestDtoAnswerFull> getAllItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET/requests от пользователя id = {}", userId);
        return itemRequestService.getAllItemRequestsOfUser(userId);
    }

    /*GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
    Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
    Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов
    для отображения.*/
    @GetMapping("/all")
    public List<ItemRequestDtoAnswerFull>
    getAllItemRequestsByParams(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Positive(message = "Значение from должно быть позитивным")
                               @RequestParam(required = false) Integer from,
                               @Positive(message = "Значение from должно быть позитивным")
                               @RequestParam(required = false) Integer size) {
        log.info("Получен запрос GET/requests/all от пользователя id = {} на вывод запросов, начиная со странницы {}, " +
                "выводить по {} элементов", userId, from, size);
        return itemRequestService.getAllItemRequestsByParams(userId, from, size);
    }

    /*GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него
    в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.*/
    @GetMapping("/{requestId}")
    public ItemRequestDtoAnswerFull getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long requestId) {
        log.info("Получен запрос GET/requests/{requestId} от пользователя id = {} " +
                "на получение запроса id = {}", userId, requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }

}
