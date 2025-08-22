package com.popcornpalace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popcornpalace.dto.BookingDto;
import com.popcornpalace.service.IBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto testBookingDto;

    @BeforeEach
    void setUp() {
        testBookingDto = BookingDto.builder()
                .id(1L)
                .showtimeId(1L)
                .seatId(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build();
    }

    @Test
    void createBooking_Success() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingDto.class))).thenReturn(testBookingDto);

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.showtimeId").value(1))
                .andExpect(jsonPath("$.seatId").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerEmail").value("john@example.com"));

        verify(bookingService).createBooking(any(BookingDto.class));
    }

    @Test
    void createBooking_ValidationError_EmptyName() throws Exception {
        // Given
        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(1L)
                .seatId(1L)
                .customerName("") // Invalid: empty name
                .customerEmail("john@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ValidationError_InvalidEmail() throws Exception {
        // Given
        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(1L)
                .seatId(1L)
                .customerName("John Doe")
                .customerEmail("invalid-email") // Invalid: wrong email format
                .build();

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ValidationError_NullShowtimeId() throws Exception {
        // Given
        BookingDto invalidDto = BookingDto.builder()
                .seatId(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build(); // Missing showtimeId

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ValidationError_NullSeatId() throws Exception {
        // Given
        BookingDto invalidDto = BookingDto.builder()
                .showtimeId(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .build(); // Missing seatId

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
