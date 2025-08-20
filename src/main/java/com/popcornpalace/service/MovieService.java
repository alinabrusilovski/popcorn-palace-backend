package com.popcornpalace.service;

import com.popcornpalace.dto.MovieDto;
import com.popcornpalace.entity.Movie;
import com.popcornpalace.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    //  Create a new movie
    public MovieDto createMovie(MovieDto movieDto) {
        // Check if movie with same title already exists
        if (movieRepository.existsByTitleIgnoreCase(movieDto.getTitle())) {
            throw new IllegalArgumentException("Movie with title '" + movieDto.getTitle() + "' already exists");
        }

        Movie movie = Movie.builder()
                .title(movieDto.getTitle())
                .genre(movieDto.getGenre())
                .duration(movieDto.getDuration())
                .rating(movieDto.getRating())
                .releaseYear(movieDto.getReleaseYear())
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return convertToDto(savedMovie);
    }

    //  Update movie
    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));

        // Check if new title conflicts with existing movie (excluding current movie)
        if (!movie.getTitle().equalsIgnoreCase(movieDto.getTitle()) &&
                movieRepository.existsByTitleIgnoreCase(movieDto.getTitle())) {
            throw new IllegalArgumentException("Movie with title '" + movieDto.getTitle() + "' already exists");
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
            throw new IllegalArgumentException("Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
    }

    //  Get all movies
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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

////  Get movie by ID
//    @Transactional(readOnly = true)
//    public MovieDto getMovieById(Long id) {
//        Movie movie = movieRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));
//        return convertToDto(movie);
//    }
//
//
////  Search movies by title
//    @Transactional(readOnly = true)
//    public List<MovieDto> searchMoviesByTitle(String title) {
//        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title);
//        return movies.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
////  Get movies by genre
//    @Transactional(readOnly = true)
//    public List<MovieDto> getMoviesByGenre(String genre) {
//        List<Movie> movies = movieRepository.findByGenreIgnoreCase(genre);
//        return movies.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
////  Get movies by release year
//    @Transactional(readOnly = true)
//    public List<MovieDto> getMoviesByReleaseYear(Integer year) {
//        List<Movie> movies = movieRepository.findByReleaseYear(year);
//        return movies.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
////  Get movies by minimum rating
//    @Transactional(readOnly = true)
//    public List<MovieDto> getMoviesByMinRating(Double minRating) {
//        List<Movie> movies = movieRepository.findByRatingGreaterThanEqual(minRating);
//        return movies.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//

}
