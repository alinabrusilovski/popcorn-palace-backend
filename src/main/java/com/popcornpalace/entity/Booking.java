package com.popcornpalace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"showtime_id", "seat_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    @NotNull(message = "Showtime is required")
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    @NotNull(message = "Seat is required")
    private Seat seat;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    @Column(nullable = false)
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    @Column(nullable = false)
    private String customerEmail;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be at least 0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @NotNull(message = "Booking date is required")
    @Column(nullable = false)
    private LocalDateTime bookingDate;
}
