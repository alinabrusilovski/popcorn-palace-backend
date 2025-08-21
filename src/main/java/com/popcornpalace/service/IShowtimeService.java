package com.popcornpalace.service;

import com.popcornpalace.dto.ShowtimeDto;
import org.springframework.stereotype.Service;

@Service
public interface IShowtimeService {

    ShowtimeDto createShowtime(ShowtimeDto showtimeDto);

    ShowtimeDto updateShowtime(Long id, ShowtimeDto showtimeDto);

    void deleteShowtime(Long id);

    ShowtimeDto getShowtimeById(Long id);
}
