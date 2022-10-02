package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                                                  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Received a request: POST/requests from user id = {} with body: {}", userId, itemRequestDto);
        LocalDateTime createDate = LocalDateTime.now();
        return itemRequestService.createItemRequest(itemRequestDto, userId, createDate);
    }

    /*GET /requests — получить список своих запросов вместе с данными об ответах на них.
    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи,
    название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию
    о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых к более старым.*/
    @GetMapping
    public List<ItemRequestDtoAnswerFull> getAllItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received a request: GET/requests from user id = {}", userId);
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
                               @RequestParam(name = "from", required = false) Integer from,
                               @RequestParam(name = "size", required = false) Integer size) {
        log.info("Received a request: GET/requests/all from user id = {} to output requests " +
                "starting from page {}, output by {} elements", userId, from, size);
        return itemRequestService.getAllItemRequestsByParams(userId, from, size);
    }

    /*GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него
    в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.*/
    @GetMapping("/{requestId}")
    public ItemRequestDtoAnswerFull getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        log.info("Received a request: GET/requests/{} from user id = {} ", requestId, userId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
