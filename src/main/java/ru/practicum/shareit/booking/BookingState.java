package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String name) {
        if (name == null) return ALL;
        try {
            return BookingState.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return ALL;
        }
    }
}