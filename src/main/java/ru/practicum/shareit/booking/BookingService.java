package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingRequestDto req) {
        User user = userService.getUser(userId);
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (item.getOwnerId() != null && item.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot book own item");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not available");
        }
        if (req.getStart() == null || req.getEnd() == null || !req.getStart().isBefore(req.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start/end");
        }

        Booking booking = new Booking();
        booking.setItemId(item.getId());
        booking.setBookerId(user.getId());
        booking.setStart(req.getStart());
        booking.setEnd(req.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return toDto(booking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can approve/reject");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return toDto(booking);
    }

    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        boolean isBooker = booking.getBookerId().equals(userId);
        boolean isOwner = item.getOwnerId() != null && item.getOwnerId().equals(userId);
        if (!isBooker && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDto(booking);
    }

    public List<BookingResponseDto> listBookingsForUser(Long userId, BookingState state) {
        userService.getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> books;

        switch (state) {
            case ALL:
                books = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                books = bookingRepository.findByBookerIdAndCurrent(userId, now);
                break;
            case PAST:
                books = bookingRepository.findByBookerIdAndPast(userId, now);
                break;
            case FUTURE:
                books = bookingRepository.findByBookerIdAndFuture(userId, now);
                break;
            case WAITING:
                books = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING.name());
                break;
            case REJECTED:
                books = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED.name());
                break;
            default:
                books = List.of();
        }

        return books.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookingResponseDto> listBookingsForOwner(Long ownerId, BookingState state) {
        userService.getUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> books;

        switch (state) {
            case ALL:
                books = bookingRepository.findByOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                books = bookingRepository.findByOwnerIdAndCurrent(ownerId, now);
                break;
            case PAST:
                books = bookingRepository.findByOwnerIdAndPast(ownerId, now);
                break;
            case FUTURE:
                books = bookingRepository.findByOwnerIdAndFuture(ownerId, now);
                break;
            case WAITING:
                books = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING.name());
                break;
            case REJECTED:
                books = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED.name());
                break;
            default:
                books = List.of();
        }

        return books.stream().map(this::toDto).collect(Collectors.toList());
    }

    private BookingResponseDto toDto(Booking b) {
        User booker = userService.getUser(b.getBookerId());
        Item item = itemRepository.findById(b.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        BookerDto bookerDto = new BookerDto(booker.getId());
        ItemDto itemDto = new ItemDto(item.getId(), item.getName());

        return new BookingResponseDto(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                b.getStatus(),
                bookerDto,
                itemDto
        );
    }

    public boolean userHasPastBookingForItem(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByBookerIdAndPast(userId, now).stream()
                .anyMatch(b -> b.getItemId().equals(itemId) && b.getStatus() == BookingStatus.APPROVED);
    }
}