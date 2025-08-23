package com.popcornpalace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popcornpalace.dto.MovieDto;
import com.popcornpalace.service.IMovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDto testMovieDto;

    @BeforeEach
    void setUp() {
        testMovieDto = MovieDto.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120)
                .rating(BigDecimal.valueOf(8.5))
                .releaseYear(2024)
                .build();
    }

    @Test
    void createMovie_Success() throws Exception {
        // Given
        when(movieService.createMovie(any(MovieDto.class))).thenReturn(testMovieDto);

        // When & Then
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.rating").value(8.5))
                .andExpect(jsonPath("$.releaseYear").value(2024));

        verify(movieService).createMovie(any(MovieDto.class));
    }

    @Test
    void createMovie_ValidationError() throws Exception {
        // Given
        MovieDto invalidDto = MovieDto.builder()
                .title("") // Invalid: empty title
                .genre("Action")
                .durationMinutes(-10) // Invalid: negative duration
                .rating(BigDecimal.valueOf(15.0)) // Invalid: rating > 10
                .releaseYear(1800) // Invalid: too old
                .build();

        // When & Then
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_Success() throws Exception {
        // Given
        List<MovieDto> movies = List.of(
                MovieDto.builder().id(1L).title("Movie 1").genre("Action").durationMinutes(120).rating(BigDecimal.valueOf(8.0)).releaseYear(2024).build(),
                MovieDto.builder().id(2L).title("Movie 2").genre("Comedy").durationMinutes(90).rating(BigDecimal.valueOf(7.5)).releaseYear(2023).build()
        );
        when(movieService.getAllMovies()).thenReturn(movies);

        // When & Then
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Movie 2"));

        verify(movieService).getAllMovies();
    }


    @Test
    void updateMovie_Success() throws Exception {
        // Given
        MovieDto updateDto = MovieDto.builder()
                .title("Updated Movie")
                .genre("Drama")
                .durationMinutes(150)
                .rating(BigDecimal.valueOf(9.0))
                .releaseYear(2025)
                .build();

        MovieDto updatedMovie = MovieDto.builder()
                .id(1L)
                .title("Updated Movie")
                .genre("Drama")
                .durationMinutes(150)
                .rating(BigDecimal.valueOf(9.0))
                .releaseYear(2025)
                .build();

        when(movieService.updateMovie(eq(1L), any(MovieDto.class))).thenReturn(updatedMovie);

        // When & Then
        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Movie"))
                .andExpect(jsonPath("$.genre").value("Drama"));

        verify(movieService).updateMovie(eq(1L), any(MovieDto.class));
    }

    @Test
    void updateMovie_ValidationError() throws Exception {
        // Given
        MovieDto invalidDto = MovieDto.builder()
                .title("") // Invalid: empty title
                .genre("Action")
                .durationMinutes(120)
                .rating(BigDecimal.valueOf(8.5))
                .releaseYear(2024)
                .build();

        // When & Then
        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovie_Success() throws Exception {
        // Given
        doNothing().when(movieService).deleteMovie(1L);

        // When & Then
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }
}
