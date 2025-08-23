package com.popcornpalace.service;

import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.entity.Theater;
import com.popcornpalace.exception.ConflictException;
import com.popcornpalace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Movie testMovie;
    private Theater testTheater;
    private Showtime testShowtime;
    private ShowtimeDto testShowtimeDto;

    @BeforeEach
    void setUp() {
        testMovie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120) // 2 hours
                .rating(new BigDecimal("8.5"))
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
                .endTime(LocalDateTime.of(2024, 12, 25, 20, 0)) // 18:00 + 2 hours = 20:00
                .price(new BigDecimal("15.00"))
                .build();

        testShowtimeDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 20, 0)) // 18:00 + 2 hours = 20:00
                .price(new BigDecimal("15.00"))
                .build();
    }

    @Test
    void createShowtime_Success() {
        // Given
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(testShowtime);

        // When
        ShowtimeDto result = showtimeService.createShowtime(testShowtimeDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMovieId()).isEqualTo(1L);
        assertThat(result.getTheaterId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(15.0);

        verify(showtimeRepository).save(any(Showtime.class));
    }

    @Test
    void createShowtime_OverlappingShowtime_ThrowsConflictException() {
        // Given
        Showtime existingShowtime = Showtime.builder()
                .id(2L)
                .movie(testMovie)
                .theater(testTheater)
                .startTime(LocalDateTime.of(2024, 12, 25, 19, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 21, 0)) // 19:00 + 2 hours = 21:00
                .price(new BigDecimal("15.00"))
                .build();

        when(showtimeRepository.existsOverlappingShowtime(
                eq(testTheater.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> showtimeService.createShowtime(testShowtimeDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Overlapping showtime");

        verify(showtimeRepository).existsOverlappingShowtime(
                eq(testTheater.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void createShowtime_InvalidEndTime_ThrowsIllegalArgumentException() {
        // Given - endTime doesn't match startTime + movie duration
        ShowtimeDto invalidDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 19, 30)) // Wrong: should be 20:00 (18:00 + 2 hours)
                .price(new BigDecimal("15.00"))
                .build();

        // When & Then
        assertThatThrownBy(() -> showtimeService.createShowtime(invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End time must equal start time plus movie duration");
    }


    @Test
    void getShowtimeById_Success() {
        // Given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));

        // When
        ShowtimeDto result = showtimeService.getShowtimeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMovieId()).isEqualTo(1L);
        assertThat(result.getTheaterId()).isEqualTo(1L);

        verify(showtimeRepository).findById(1L);
    }

    @Test
    void getShowtimeById_NotFound() {
        // Given
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showtimeService.getShowtimeById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Showtime not found");

        verify(showtimeRepository).findById(999L);
    }

    @Test
    void updateShowtime_Success() {
        // Given
        ShowtimeDto updateDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 19, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 21, 0))
                .price(new BigDecimal("20.00"))
                .build();

        Showtime updatedShowtime = Showtime.builder()
                .id(1L)
                .movie(testMovie)
                .theater(testTheater)
                .startTime(LocalDateTime.of(2024, 12, 25, 19, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 21, 0))
                .price(new BigDecimal("20.00"))
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        when(showtimeRepository.existsOverlappingShowtime(
                eq(testTheater.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(false);
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(updatedShowtime);

        // When
        ShowtimeDto result = showtimeService.updateShowtime(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(20.0);
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2024, 12, 25, 19, 0));

        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository).save(any(Showtime.class));
    }

    @Test
    void updateShowtime_NotFound() {
        // Given
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showtimeService.updateShowtime(999L, testShowtimeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Showtime not found");

        verify(showtimeRepository).findById(999L);
        verify(showtimeRepository, never()).save(any());
    }

    @Test
    void deleteShowtime_Success() {
        // Given
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));
        doNothing().when(showtimeRepository).delete(testShowtime);

        // When
        showtimeService.deleteShowtime(1L);

        // Then
        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository).delete(testShowtime);
    }

    @Test
    void deleteShowtime_NotFound() {
        // Given
        when(showtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showtimeService.deleteShowtime(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Showtime not found");

        verify(showtimeRepository).findById(999L);
        verify(showtimeRepository, never()).delete(any());
    }
}
