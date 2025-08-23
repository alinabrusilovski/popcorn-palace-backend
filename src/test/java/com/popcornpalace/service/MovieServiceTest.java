package com.popcornpalace.service;

import com.popcornpalace.dto.MovieDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;
    private MovieDto testMovieDto;

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

        testMovieDto = MovieDto.builder()
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120)
                .rating(BigDecimal.valueOf(8.5))
                .releaseYear(2024)
                .build();
    }

    @Test
    void createMovie_Success() {
        // Given
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // When
        MovieDto result = movieService.createMovie(testMovieDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Movie");
        assertThat(result.getGenre()).isEqualTo("Action");
        assertThat(result.getDurationMinutes()).isEqualTo(120);
        assertThat(result.getRating()).isEqualTo(8.5);
        assertThat(result.getReleaseYear()).isEqualTo(2024);
        
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void getAllMovies_Success() {
        // Given
        Movie movie1 = Movie.builder().id(1L).title("Movie 1").genre("Action").durationMinutes(120).rating(BigDecimal.valueOf(8.0)).releaseYear(2024).build();
        Movie movie2 = Movie.builder().id(2L).title("Movie 2").genre("Comedy").durationMinutes(90).rating(BigDecimal.valueOf(7.5)).releaseYear(2023).build();
        
        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));

        // When
        List<MovieDto> result = movieService.getAllMovies();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Movie 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Movie 2");
        
        verify(movieRepository).findAll();
    }



    @Test
    void updateMovie_Success() {
        // Given
        MovieDto updateDto = MovieDto.builder()
                .title("Updated Movie")
                .genre("Drama")
                .durationMinutes(150)
                .rating(BigDecimal.valueOf(9.0))
                .releaseYear(2025)
                .build();

        Movie updatedMovie = Movie.builder()
                .id(1L)
                .title("Updated Movie")
                .genre("Drama")
                .durationMinutes(150)
                .rating(BigDecimal.valueOf(9.0))
                .releaseYear(2025)
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        // When
        MovieDto result = movieService.updateMovie(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Movie");
        assertThat(result.getGenre()).isEqualTo("Drama");
        assertThat(result.getDurationMinutes()).isEqualTo(150);
        assertThat(result.getRating()).isEqualTo(9.0);
        assertThat(result.getReleaseYear()).isEqualTo(2025);
        
        verify(movieRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_NotFound() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(999L, testMovieDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");
        
        verify(movieRepository).findById(999L);
        verify(movieRepository, never()).save(any());
    }

    @Test
    void deleteMovie_Success() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        doNothing().when(movieRepository).delete(testMovie);

        // When
        movieService.deleteMovie(1L);

        // Then
        verify(movieRepository).findById(1L);
        verify(movieRepository).delete(testMovie);
    }

    @Test
    void deleteMovie_NotFound() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");
        
        verify(movieRepository).findById(999L);
        verify(movieRepository, never()).delete(any());
    }
}
