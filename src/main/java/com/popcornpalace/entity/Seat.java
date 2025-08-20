package com.popcornpalace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    @NotNull(message = "Theater is required")
    private Theater theater;

    @NotBlank(message = "Seat number is required")
    @Size(max = 10, message = "Seat number cannot exceed 10 characters")
    @Column(nullable = false)
    private String seatNumber;

    @NotBlank(message = "Row is required")
    @Size(max = 5, message = "Row cannot exceed 5 characters")
    @Column(nullable = false)
    private String row;

    @NotNull(message = "Seat type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    public enum SeatType {
        REGULAR, PREMIUM, VIP
    }
}
