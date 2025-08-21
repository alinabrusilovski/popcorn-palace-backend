package com.popcornpalace.repository;

import com.popcornpalace.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    //  Check if movie with title exists (case-insensitive)
    boolean existsByTitleIgnoreCase(String title);

}
