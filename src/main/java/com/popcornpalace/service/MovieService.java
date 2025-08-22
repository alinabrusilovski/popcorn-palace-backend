package com.popcornpalace.service;

import com.popcornpalace.dto.MovieDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.exception.ConflictException;
import com.popcornpalace.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class MovieService implements IMovieService {

    private final MovieRepository movieRepository;

    //  Create a new movie
    public MovieDto createMovie(MovieDto movieDto) {
        // Check if movie with same title already exists
        if (movieRepository.existsByTitleIgnoreCase(movieDto.getTitle())) {
            throw new ConflictException(
                    "Movie with title '" + movieDto.getTitle() + "' already exists"); // 409
        }

        Movie movie = Movie.builder()
                .title(movieDto.getTitle())
                .genre(movieDto.getGenre())
                .duration(movieDto.getDuration())
                .rating(movieDto.getRating())
                .releaseYear(movieDto.getReleaseYear())
                .build();
        try {
            Movie savedMovie = movieRepository.save(movie);
            return convertToDto(savedMovie);
        } catch (DataIntegrityViolationException e) {
            // In case of a race with UNIQUE(title) in the database
            throw new ConflictException(
                    "Movie with title '" + movieDto.getTitle() + "' already exists");
        }
    }

    //  Update movie
    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movie not found: " + id)); // 404

        // Check if new title conflicts with existing movie (excluding current movie)
        if (!movie.getTitle().equalsIgnoreCase(movieDto.getTitle()) &&
                movieRepository.existsByTitleIgnoreCase(movieDto.getTitle())) {
            throw new ConflictException(
                    "Movie with title '" + movieDto.getTitle() + "' already exists"); // 409
        }

        movie.setTitle(movieDto.getTitle());
        movie.setGenre(movieDto.getGenre());
        movie.setDuration(movieDto.getDuration());
        movie.setRating(movieDto.getRating());
        movie.setReleaseYear(movieDto.getReleaseYear());

        Movie updatedMovie = movieRepository.save(movie);
        return convertToDto(updatedMovie);
    }

    //  Delete movie
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Movie not found: " + id); // 404
        }
        movieRepository.deleteById(id);
    }

    //  Get all movies
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::convertToDto)
                .toList();
    }



    //  Convert Movie entity to MovieDto
    private MovieDto convertToDto(Movie movie) {
        return MovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .releaseYear(movie.getReleaseYear())
                .build();
    }
}
