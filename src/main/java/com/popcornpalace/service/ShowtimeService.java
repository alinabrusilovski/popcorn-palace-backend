package com.popcornpalace.service;

import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.entity.Theater;
import com.popcornpalace.exception.ConflictException;
import com.popcornpalace.repository.MovieRepository;
import com.popcornpalace.repository.ShowtimeRepository;
import com.popcornpalace.repository.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Transactional
@RequiredArgsConstructor
public class ShowtimeService implements IShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    private static final Duration GAP = Duration.ofHours(1);

    @Override
    public ShowtimeDto createShowtime(ShowtimeDto showtimeDto) {

        Movie movie = movieRepository.findById(showtimeDto.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movie not found: " + showtimeDto.getMovieId()));

        Theater theater = theaterRepository.findById(showtimeDto.getTheaterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Theater not found: " + showtimeDto.getMovieId()));

        if (showtimeDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Start time must be in the future");
        }

        if (showtimeDto.getEndTime().isBefore(showtimeDto.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        // end = start + movie.durationMinutes
        Integer durationMinutes = movie.getDurationMinutes();
        if (durationMinutes == null) {
            throw new IllegalArgumentException("Movie duration is not set");
        }
        LocalDateTime expectedEnd = showtimeDto.getStartTime().plusMinutes(durationMinutes);
        if (!sameMinute(expectedEnd, showtimeDto.getEndTime())) {
            throw new IllegalArgumentException("End time must equal start time plus movie duration");
        }

        // extend the interval by 1 hour before and after - to check the buffer
        LocalDateTime bufferedStart = showtimeDto.getStartTime().minus(GAP);
        LocalDateTime bufferedEnd = expectedEnd.plus(GAP);

        // Check for overlapping showtimes in the same theater
        if (showtimeRepository.existsOverlappingShowtime(
                showtimeDto.getTheaterId(),
                bufferedStart,
                bufferedEnd
        )) {
            throw new ConflictException(
                    "Showtime overlaps with existing showtime in the same theater"); // 409
        }

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .theater(theater)
                .startTime(showtimeDto.getStartTime())
                .endTime(expectedEnd)
                .price(showtimeDto.getPrice())
                .build();

        Showtime savedShowtime = showtimeRepository.save(showtime);
        return convertToDto(savedShowtime);
    }

    @Override
    public ShowtimeDto updateShowtime(Long id, ShowtimeDto showtimeDto) {

        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Showtime not found: " + id)); // 404

        Movie movie = movieRepository.findById(showtimeDto.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movie not found: " + showtimeDto.getMovieId())); // 404

        Theater theater = theaterRepository.findById(showtimeDto.getTheaterId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Theater not found: " + showtimeDto.getTheaterId())); //404


        if (showtimeDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Start time must be in the future");
        }

        if (showtimeDto.getEndTime().isBefore(showtimeDto.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        // end = start + movie.durationMinutes
        Integer durationMinutes = movie.getDurationMinutes();
        if (durationMinutes == null) {
            throw new IllegalArgumentException("Movie duration is not set");
        }
        LocalDateTime expectedEnd = showtimeDto.getStartTime().plusMinutes(durationMinutes);
        if (!sameMinute(expectedEnd, showtimeDto.getEndTime())) {
            throw new IllegalArgumentException("End time must equal start time plus movie duration");
        }

        // extend the interval by 1 hour before and after - to check the buffer
        LocalDateTime bufferedStart = showtimeDto.getStartTime().minus(GAP);
        LocalDateTime bufferedEnd = expectedEnd.plus(GAP);

        // Check for overlapping showtimes in the same theater (excluding current showtime)
        if (showtimeRepository.existsOverlappingShowtimeExcluding(
                showtimeDto.getTheaterId(),
                bufferedStart,
                bufferedEnd,
                id)) {
            throw new ConflictException(
                    "Showtime overlaps with existing showtime in the same theater");
        }

        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setStartTime(showtimeDto.getStartTime());
        showtime.setEndTime(expectedEnd);
        showtime.setPrice(showtimeDto.getPrice());

        Showtime updatedShowtime = showtimeRepository.save(showtime);
        return convertToDto(updatedShowtime);
    }

    //    Delete showtime
    @Override
    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Showtime not found: " + id); // 404
        }
        showtimeRepository.deleteById(id);
    }

    //    Get showtime by ID
    @Transactional(readOnly = true)
    public ShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Showtime not found: " + id)); // 404
        return convertToDto(showtime);
    }

    private static boolean sameMinute(LocalDateTime a, LocalDateTime b) {
        return a.truncatedTo(ChronoUnit.MINUTES).equals(b.truncatedTo(ChronoUnit.MINUTES));
    }

    private ShowtimeDto convertToDto(Showtime showtime) {
        return ShowtimeDto.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .theaterId(showtime.getTheater().getId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .price(showtime.getPrice())
                .build();
    }
}
