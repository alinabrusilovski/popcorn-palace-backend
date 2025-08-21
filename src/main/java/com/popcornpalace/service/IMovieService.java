package com.popcornpalace.service;

import com.popcornpalace.dto.MovieDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface IMovieService {

    MovieDto createMovie(MovieDto movieDto);

    MovieDto updateMovie(Long id, MovieDto movieDto);

    void deleteMovie(Long id);

    List<MovieDto> getAllMovies();

}
