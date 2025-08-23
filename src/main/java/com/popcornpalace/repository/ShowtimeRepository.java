package com.popcornpalace.repository;

import com.popcornpalace.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    //Check for overlapping showtimes in the same theater
    @Query("""
                select exists(
                 select 1
                 from Showtime s
                 where s.theater.id = :theaterId
                  and s.startTime < :endTime
                  and s.endTime   > :startTime
                )
            """)
    boolean existsOverlappingShowtime(@Param("theaterId") Long theaterId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    //    Check for overlaping showtimes excluding a specific showtime (for updates)
    @Query("""
                select exists(
                 select 1
                 from Showtime s
                 where s.theater.id = :theaterId
                  and s.id <> :excludeId
                  and s.startTime < :endTime
                  and s.endTime   > :startTime
                )
            """)
    boolean existsOverlappingShowtimeExcluding(@Param("theaterId") Long theaterId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("excludeId") Long excludeId);
}
