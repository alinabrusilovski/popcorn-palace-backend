package com.popcornpalace.service;

import com.popcornpalace.dto.BookingDto;
import com.popcornpalace.entity.Booking;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.entity.Seat;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.entity.Theater;
import com.popcornpalace.exception.ConflictException;
import com.popcornpalace.repository.BookingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.popcornpalace.repository.SeatRepository;
import com.popcornpalace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private BookingService bookingService;

    private Movie testMovie;
    private Theater testTheater;
    private Showtime testShowtime;
    private Seat testSeat;
    private Booking testBooking;
    private BookingDto testBookingDto;

    @BeforeEach
    void setUp() {
        testMovie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120)
                .rating(BigDecimal.valueOf(8.5))
                .releaseYear(2024)
                .build();

        testTheater = Theater.builder()
                .id(1L)
                .name("Test Theater")
                .capacity(100)
                .build();

        testShowtime = Showtime.builder()
                .id(1L)
                .movie(testMovie)
                .theater(testTheater)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 20, 0))
                .price(new BigDecimal("15.00"))
                .build();

        testSeat = Seat.builder()
                .id(1L)
                .row("A")
                .seatNumber("1")
                .seatType(Seat.SeatType.REGULAR)
                .theater(testTheater)
                .build();

        testBooking = Booking.builder()
                .id(1L)
                .showtime(testShowtime)
                .seat(testSeat)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .totalPrice(new BigDecimal("15.00"))
                .bookingDate(OffsetDateTime.now())
                .build();

        testBookingDto = BookingDto.builder()
                .showtimeId(1L)
                .seatId(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build();
    }

    @Test
    void createBooking_Success() {
        // Given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        BookingDto result = bookingService.createBooking(testBookingDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getShowtimeId()).isEqualTo(1L);
        assertThat(result.getSeatId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("John Doe");
        assertThat(result.getCustomerEmail()).isEqualTo("john@example.com");

        verify(showtimeRepository).findById(1L);
        verify(seatRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ShowtimeNotFound_ThrowsException() {
        // Given
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(999L)
                .seatId(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build();

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(invalidDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Showtime not found");

        verify(showtimeRepository).findById(999L);
        verify(seatRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_SeatNotFound_ThrowsException() {
        // Given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(1L)
                .seatId(999L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build();

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(invalidDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Seat not found");

        verify(showtimeRepository).findById(1L);
        verify(seatRepository).findById(999L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_SeatAlreadyBooked_ThrowsConflictException() {
        // Given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(testBookingDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Seat is already booked");

        verify(showtimeRepository).findById(1L);
        verify(seatRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_SeatFromDifferentTheater_ThrowsException() {
        // Given
        Theater differentTheater = Theater.builder()
                .id(2L)
                .name("Different Theater")
                .capacity(50)
                .build();

        Seat seatFromDifferentTheater = Seat.builder()
                .id(2L)
                .row("A")
                .seatNumber("1")
                .seatType(Seat.SeatType.REGULAR)
                .theater(differentTheater)
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(seatFromDifferentTheater));

        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(1L)
                .seatId(2L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build();

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seat does not belong to the showtime's theater");

        verify(showtimeRepository).findById(1L);
        verify(seatRepository).findById(2L);
        verify(bookingRepository, never()).save(any());
    }
}
