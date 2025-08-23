package com.popcornpalace.controller;

import com.popcornpalace.dto.MovieDto;
import com.popcornpalace.service.MovieService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie Management", description = "APIs for managing movies")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Create a new movie")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto created = movieService.createMovie(movieDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody MovieDto movieDto) {
        return ResponseEntity.ok(movieService.updateMovie(id, movieDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete movie")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/problem+json"))
    public ResponseEntity<Void> deleteMovie(@PathVariable @NotNull @Positive Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all movies")
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }
}
