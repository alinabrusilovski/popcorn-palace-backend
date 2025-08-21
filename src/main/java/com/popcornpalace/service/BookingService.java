package com.popcornpalace.service;

import com.popcornpalace.dto.BookingDto;
import com.popcornpalace.entity.Booking;
import com.popcornpalace.entity.Seat;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.repository.BookingRepository;
import com.popcornpalace.repository.SeatRepository;
import com.popcornpalace.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        // Validate showtime exists and is in the future
        Showtime showtime = showtimeRepository.findById(bookingDto.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + bookingDto.getShowtimeId()));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book tickets for past showtimes");
        }

        // Validate seat exists and belongs to the same theater as the showtime
        Seat seat = seatRepository.findById(bookingDto.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with ID: " + bookingDto.getSeatId()));

        if (!seat.getTheater().getId().equals(showtime.getTheater().getId())) {
            throw new IllegalArgumentException("Seat does not belong to the theater of the selected showtime");
        }

        BigDecimal price = showtime.getPrice();

//        // Check if seat is already booked for this showtime
//        if (bookingRepository.isSeatBooked(bookingDto.getShowtimeId(), bookingDto.getSeatId())) {
//            throw new IllegalArgumentException("Seat is already booked for this showtime");
//        }

        Booking booking = Booking.builder()
                .showtime(showtime)
                .seat(seat)
                .customerName(bookingDto.getCustomerName())
                .customerEmail(bookingDto.getCustomerEmail())
                .totalPrice(price)
                .bookingDate(LocalDateTime.now())
                .build();


        //with @Transactional the actual unique index check is often triggered
        // at the flush/commit stage - after returning from the method
        // so I Force flush inside try
        try {
            Booking savedBooking = bookingRepository.saveAndFlush(booking);
            return convertToDto(savedBooking);
        } catch (DataIntegrityViolationException e) {
            // Unique key worked (place already taken for this session)
            throw new IllegalArgumentException("Seat is already booked for this showtime");
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
