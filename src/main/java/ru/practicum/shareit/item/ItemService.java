package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public Item addItem(Item item, Long userId) {
        userService.getUser(userId);
        item.setOwnerId(userId);

        if(item.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability must be specified");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description is required");
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item patch, Long userId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!existing.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can update");
        }
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) existing.setAvailable(patch.getAvailable());
        return itemRepository.save(existing);
    }

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    public List<Item> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId);
    }

    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) return List.of();
        String lower = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lower)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public ItemResponseDto getItemWithDetails(Long itemId, Long userId) {
        Item item = getItem(itemId);

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwnerId().equals(userId)) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }

        List<CommentResponseDto> comments = getCommentsForItem(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());

        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    private BookingShortDto getLastBooking(Long itemId) {
        List<Booking> pastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(
                itemId, LocalDateTime.now());

        if (!pastBookings.isEmpty()) {
            Booking last = pastBookings.get(0);
            return new BookingShortDto(last.getId(), last.getBookerId());
        }
        return null;
    }

    private BookingShortDto getNextBooking(Long itemId) {
        List<Booking> futureBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(
                itemId, LocalDateTime.now());

        if (!futureBookings.isEmpty()) {
            Booking next = futureBookings.get(0);
            return new BookingShortDto(next.getId(), next.getBookerId());
        }
        return null;
    }

    private CommentResponseDto toCommentDto(Comment comment) {
        User author = userService.getUser(comment.getAuthorId());

        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                author.getName(),
                comment.getCreated()
        );
    }

    public CommentResponseDto addComment(Long itemId, Long userId, String text) {
        getItem(itemId);
        userService.getUser(userId);

        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    public List<Comment> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId);
    }
}