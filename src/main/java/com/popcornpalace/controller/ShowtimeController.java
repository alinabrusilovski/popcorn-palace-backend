package com.popcornpalace.controller;

import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/showtimes")
@Tag(name = "Showtime Management", description = "APIs for managing showtmes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    @Operation(summary = "Create a new showtime", description = "Add a new showtime for a movie in a theater")
    public ResponseEntity<ShowtimeDto> createShowtime(@Valid @RequestBody ShowtimeDto showtimeDto) {
        try {
            ShowtimeDto createdShowtime = showtimeService.createShowtime(showtimeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get showtime by ID", description = "Retrieve a specific showtime by its ID")
    public ResponseEntity<ShowtimeDto> getShowtimeById(@PathVariable Long id) {
        try {
            ShowtimeDto showtime = showtimeService.getShowtimeById(id);
            return ResponseEntity.ok(showtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update showtime", description = "Update an existing showtime's information")
    public ResponseEntity<ShowtimeDto> updateShowtime(@PathVariable Long id, @Valid @RequestBody ShowtimeDto showtimeDto) {
        try {
            ShowtimeDto updatedShowtime = showtimeService.updateShowtime(id, showtimeDto);
            return ResponseEntity.ok(updatedShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete showtime", description = "Remove a showtime from the system")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
