package com.popcornpalace.service;

import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.entity.Showtime;
import com.popcornpalace.entity.Theater;
import com.popcornpalace.repository.MovieRepository;
import com.popcornpalace.repository.ShowtimeRepository;
import com.popcornpalace.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
public class ShowtimeService implements IShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    @Override
    public ShowtimeDto createShowtime(ShowtimeDto showtimeDto) {

        Movie movie = movieRepository.findById(showtimeDto.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + showtimeDto.getMovieId()));

        Theater theater = theaterRepository.findById(showtimeDto.getTheaterId())
                .orElseThrow(() -> new IllegalArgumentException("Theater not found with ID: " + showtimeDto.getTheaterId()));

        if (showtimeDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }

        if (showtimeDto.getEndTime().isBefore(showtimeDto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping showtimes in the same theater
        if (showtimeRepository.existsOverlappingShowtime(
                showtimeDto.getTheaterId(),
                showtimeDto.getStartTime(),
                showtimeDto.getEndTime())) {
            throw new IllegalArgumentException("Showtime overlaps with existing showtime in the same theater");
        }

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .theater(theater)
                .startTime(showtimeDto.getStartTime())
                .endTime(showtimeDto.getEndTime())
                .price(showtimeDto.getPrice())
                .build();

        Showtime savedShowtime = showtimeRepository.save(showtime);
        return convertToDto(savedShowtime);
    }

    @Override
    public ShowtimeDto updateShowtime(Long id, ShowtimeDto showtimeDto) {

        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + id));

        Movie movie = movieRepository.findById(showtimeDto.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + showtimeDto.getMovieId()));

        Theater theater = theaterRepository.findById(showtimeDto.getTheaterId())
                .orElseThrow(() -> new IllegalArgumentException("Theater not found with ID: " + showtimeDto.getTheaterId()));

        if (showtimeDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }

        if (showtimeDto.getEndTime().isBefore(showtimeDto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping showtimes in the same theater (excluding current showtime)
        if (showtimeRepository.existsOverlappingShowtimeExcluding(
                showtimeDto.getTheaterId(),
                showtimeDto.getStartTime(),
                showtimeDto.getEndTime(),
                id)) {
            throw new IllegalArgumentException("Showtime overlaps with existing showtime in the same theater");
        }

        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setStartTime(showtimeDto.getStartTime());
        showtime.setEndTime(showtimeDto.getEndTime());
        showtime.setPrice(showtimeDto.getPrice());

        Showtime updatedShowtime = showtimeRepository.save(showtime);
        return convertToDto(updatedShowtime);
    }

    //    Delete showtime
    @Override
    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new IllegalArgumentException("Showtime not found with ID: " + id);
        }
        showtimeRepository.deleteById(id);
    }

    //    Get showtime by ID
    @Transactional(readOnly = true)
    public ShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + id));
        return convertToDto(showtime);
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
