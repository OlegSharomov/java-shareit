package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReqDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsOfUser(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemReqDto itemDto) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemReqDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> searchForItemsByQueryText(Long userId, String text) {
        Map<String, Object> searchText = new HashMap<>();
        searchText.put("text", text);
        return get("/search?text={text}", userId, searchText);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentRequestDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}
