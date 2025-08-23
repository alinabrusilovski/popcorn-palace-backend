package com.popcornpalace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.service.IShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowtimeController.class)
class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IShowtimeService showtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    private ShowtimeDto testShowtimeDto;

    @BeforeEach
    void setUp() {
        testShowtimeDto = ShowtimeDto.builder()
                .id(1L)
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 20, 0))
                .price(new BigDecimal("15.00"))
                .build();
    }

    @Test
    void createShowtime_Success() throws Exception {
        // Given
        when(showtimeService.createShowtime(any(ShowtimeDto.class))).thenReturn(testShowtimeDto);

        // When & Then
        mockMvc.perform(post("/api/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testShowtimeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.movieId").value(1))
                .andExpect(jsonPath("$.theaterId").value(1))
                .andExpect(jsonPath("$.price").value(15.0));

        verify(showtimeService).createShowtime(any(ShowtimeDto.class));
    }

    @Test
    void createShowtime_ValidationError() throws Exception {
        // Given
        ShowtimeDto invalidDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 17, 0)) // Invalid: end before start
                .price(new BigDecimal("-5.00")) // Invalid: negative price
                .build();

        // When & Then
        mockMvc.perform(post("/api/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getShowtimeById_Success() throws Exception {
        // Given
        when(showtimeService.getShowtimeById(1L)).thenReturn(testShowtimeDto);

        // When & Then
        mockMvc.perform(get("/api/showtimes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.movieId").value(1));

        verify(showtimeService).getShowtimeById(1L);
    }

    @Test
    void getShowtimeById_NotFound() throws Exception {
        // Given
        when(showtimeService.getShowtimeById(999L)).thenThrow(new RuntimeException("Showtime not found"));

        // When & Then
        mockMvc.perform(get("/api/showtimes/999"))
                .andExpect(status().isInternalServerError());

        verify(showtimeService).getShowtimeById(999L);
    }

    @Test
    void updateShowtime_Success() throws Exception {
        // Given
        ShowtimeDto updateDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 19, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 21, 0))
                .price(new BigDecimal("20.00"))
                .build();

        ShowtimeDto updatedShowtime = ShowtimeDto.builder()
                .id(1L)
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 19, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 21, 0))
                .price(new BigDecimal("20.00"))
                .build();

        when(showtimeService.updateShowtime(eq(1L), any(ShowtimeDto.class))).thenReturn(updatedShowtime);

        // When & Then
        mockMvc.perform(put("/api/showtimes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(20.0));

        verify(showtimeService).updateShowtime(eq(1L), any(ShowtimeDto.class));
    }

    @Test
    void updateShowtime_ValidationError() throws Exception {
        // Given
        ShowtimeDto invalidDto = ShowtimeDto.builder()
                .movieId(1L)
                .theaterId(1L)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 17, 0)) // Invalid: end before start
                .price(new BigDecimal("15.00"))
                .build();

        // When & Then
        mockMvc.perform(put("/api/showtimes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShowtime_Success() throws Exception {
        // Given
        doNothing().when(showtimeService).deleteShowtime(1L);

        // When & Then
        mockMvc.perform(delete("/api/showtimes/1"))
                .andExpect(status().isNoContent());

        verify(showtimeService).deleteShowtime(1L);
    }
}
