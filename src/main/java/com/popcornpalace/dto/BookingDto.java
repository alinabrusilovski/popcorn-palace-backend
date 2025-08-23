package com.popcornpalace.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    private String customerEmail;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be at least 0.01")
    private BigDecimal totalPrice;
}
