package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto request,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam("approved") boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", required = false) String state) {
        BookingState st = BookingState.from(state);
        return bookingService.listBookingsForUser(userId, st);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                        @RequestParam(value = "state", required = false) String state) {
        BookingState st = BookingState.from(state);
        return bookingService.listBookingsForOwner(ownerId, st);
    }
}