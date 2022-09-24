package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestService;

    /*POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса,
    где пользователь описывает, какая именно вещь ему нужна.*/
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody RequestDto itemRequestDto) {
        log.info("Received a request: POST/requests from user id = {} with body: {}", userId, itemRequestDto);
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    /*GET /requests — получить список своих запросов вместе с данными об ответах на них.
    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи,
    название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию
    о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых к более старым.*/
    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received a request: GET/requests from user id = {}", userId);
        return itemRequestService.getAllItemRequestsOfUser(userId);
    }

    /*GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
    Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
    Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов
    для отображения.*/
    @GetMapping("/all")
    public ResponseEntity<Object>
    getAllItemRequestsByParams(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PositiveOrZero(message = "'from' must be positive or zero")
                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                               @Positive(message = "'size' must be positive")
                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received a request: GET/requests/all?from={}&size={} from user id = {}", from, size, userId);
        return itemRequestService.getAllItemRequestsByParams(userId, from, size);
    }

    /*GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него
    в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.*/
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Received a request: GET/requests/{} from user id = {} ", requestId, userId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}