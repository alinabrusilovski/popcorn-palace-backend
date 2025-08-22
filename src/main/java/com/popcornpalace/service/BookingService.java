package com.popcornpalace.service;

import com.popcornpalace.dto.BookingDto;
import com.popcornpalace.entity.Booking;
import com.popcornpalace.entity.Seat;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.exception.ConflictException;
import com.popcornpalace.repository.BookingRepository;
import com.popcornpalace.repository.SeatRepository;
import com.popcornpalace.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        log.info("Create booking request: showtimeId={}, seatId={}, email={}",
                bookingDto.getShowtimeId(), bookingDto.getSeatId(), bookingDto.getCustomerEmail());

        // Validate showtime exists and is in the future
        Showtime showtime = showtimeRepository.findById(bookingDto.getShowtimeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Showtime not found: " + bookingDto.getShowtimeId())); // 404

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to book past showtime: showtimeId={}", showtime.getId());
            throw new IllegalArgumentException("Cannot book tickets for past showtimes");
        }

        // Validate seat exists and belongs to the same theater as the showtime
        Seat seat = seatRepository.findById(bookingDto.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Seat not found: " + bookingDto.getSeatId())); // 404

        if (!seat.getTheater().getId().equals(showtime.getTheater().getId())) {
            throw new IllegalArgumentException("Seat does not belong to the theater of the selected showtime");
        }

        Booking booking = Booking.builder()
                .showtime(showtime)
                .seat(seat)
                .customerName(bookingDto.getCustomerName())
                .customerEmail(bookingDto.getCustomerEmail())
                .totalPrice(showtime.getPrice())
                .bookingDate(LocalDateTime.now())
                .build();

        try {
            Booking savedBooking = bookingRepository.saveAndFlush(booking);
            return convertToDto(savedBooking);
        } catch (DataIntegrityViolationException e) {
            // Unique key worked (place already taken for this session)
            throw new ConflictException("Seat is already booked for this showtime"); // 409
        }
    }

    private BookingDto convertToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .showtimeId(booking.getShowtime().getId())
                .seatId(booking.getSeat().getId())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .totalPrice(booking.getTotalPrice())
                .build();
    }
}
