package com.popcornpalace.controller;

import com.popcornpalace.dto.ShowtimeDto;
import com.popcornpalace.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@Validated
@RestController
@RequestMapping("/api/showtimes")
@Tag(name = "Showtime Management", description = "APIs for managing showtimes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    @Operation(summary = "Create a new showtime")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<ShowtimeDto> createShowtime(@Valid @RequestBody ShowtimeDto showtimeDto) {
        ShowtimeDto created = showtimeService.createShowtime(showtimeDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get showtime by ID")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<ShowtimeDto> getShowtimeById(@PathVariable @NotNull @Positive Long id) {
        return ResponseEntity.ok(showtimeService.getShowtimeById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update showtime")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<ShowtimeDto> updateShowtime(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody ShowtimeDto showtimeDto) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtimeDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete showtime")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<Void> deleteShowtime(@PathVariable @NotNull @Positive Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }

}
