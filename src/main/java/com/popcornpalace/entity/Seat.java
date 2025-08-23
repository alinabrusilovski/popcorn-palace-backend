package com.popcornpalace.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
