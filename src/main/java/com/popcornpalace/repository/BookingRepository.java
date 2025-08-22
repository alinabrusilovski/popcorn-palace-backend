package com.popcornpalace.repository;

import com.popcornpalace.entity.Booking;
import com.popcornpalace.entity.Seat;
import com.popcornpalace.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
