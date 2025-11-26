package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.BookingService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.addItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId,
                           @RequestBody Item item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.updateItem(itemId, item, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemResponseDto item = itemService.getItemWithDetails(itemId, userId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItemsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getAllItemsByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CommentRequestDto requestDto
    ) {
        if (!bookingService.userHasPastBookingForItem(userId, itemId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User hasn't completed approved booking for this item"
            );
        }
        return ResponseEntity.ok(itemService.addComment(itemId, userId, requestDto.getText()));
    }

    @GetMapping("/{itemId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getCommentsForItem(itemId));
    }
}