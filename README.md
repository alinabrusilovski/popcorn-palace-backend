# Popcorn Palace - Movie Ticket Booking System

## Project Overview
A RESTful API for a movie ticket booking system built with Spring Boot. The system manages movies, showtimes, and ticket bookings with comprehensive validation and error handling.

## Features

### Movie Management
- **POST** `/api/movies` - Add new movies
- **PUT** `/api/movies/{id}` - Update movie information
- **DELETE** `/api/movies/{id}` - Delete a movie
- **GET** `/api/movies` - Fetch all movies

**Movie Fields:**
- `title` (required, unique) - Movie title
- `genre` (required) - Movie genre
- `duration` (required) - Duration in minutes (1-480)
- `rating` (required) - Rating from 0.0 to 10.0
- `releaseYear` (required) - Release year

### Showtime Management
- **POST** `/api/showtimes` - Add showtimes for movies
- **PUT** `/api/showtimes/{id}` - Update showtime details
- **DELETE** `/api/showtimes/{id}` - Delete a showtime
- **GET** `/api/showtimes/{id}` - Fetch showtime by ID

**Showtime Fields:**
- `movie` (required) - Associated movie
- `theater` (required) - Theater location
- `startTime` (required) - Start time
- `endTime` (required) - End time
- `price` (required) - Ticket price (0.01-1000.00)

**Constraints:**
- No overlapping showtimes for the same theater

### Ticket Booking System
- **POST** `/api/bookings` - Book tickets for available showtimes

**Booking Fields:**
- `showtime` (required) - Selected showtime
- `seat` (required) - Selected seat
- `customerName` (required) - Customer name
- `customerEmail` (required, valid email) - Customer email
- `totalPrice` (required) - Total price for the booking

**Constraints:**
- No seat can be booked twice for the exact showtime

## Technology Stack
- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3**
- **Bean Validation**
- **Lombok**

## API Documentation
Once the application is running, you can access the Swagger UI at:
```
http://localhost:10001/swagger-ui.html
```

## Error Handling
The API provides comprehensive error handling with:
- **400 Bad Request** - Invalid input validation
- **404 Not Found** - Resource not found
- **409 Conflict** - Business rule violations
- **500 Internal Server Error** - Unexpected server errors

All errors return structured ProblemDetail responses with error codes and detailed messages.

## Database Schema
The system includes the following entities:
- **Movie** - Movie information
- **Theater** - Theater locations
- **Showtime** - Movie screening times
- **Seat** - Available seats in theaters
- **Booking** - Customer ticket bookings

## Security Features
- Input validation and sanitization
- SQL injection prevention through JPA
- Comprehensive error handling without information leakage

