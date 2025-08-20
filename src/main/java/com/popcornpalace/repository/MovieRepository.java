package com.popcornpalace.repository;

import com.popcornpalace.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    //  Find movie by title (case-insensitive)
    Optional<Movie> findByTitleIgnoreCase(String title);

    //  Check if movie with title exists (case-insensitive)
    boolean existsByTitleIgnoreCase(String title);

    //  Search movies by title containing search term (case-insensitive)
    List<Movie> findByTitleContainingIgnoreCase(String title);

    //  Find movies by genre (case-insensitive)
    List<Movie> findByGenreIgnoreCase(String genre);

    //  Find movies by release year
    List<Movie> findByReleaseYear(Integer releaseYear);

    //  Find movies with rating greater than or equal to specified value
    List<Movie> findByRatingGreaterThanEqual(Double rating);
}
