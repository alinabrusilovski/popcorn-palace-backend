package com.popcornpalace.service;

import com.popcornpalace.dto.BookingDto;
import org.springframework.stereotype.Service;

@Service
public interface IBookingService {

    BookingDto createBooking(BookingDto bookingDto);

}
