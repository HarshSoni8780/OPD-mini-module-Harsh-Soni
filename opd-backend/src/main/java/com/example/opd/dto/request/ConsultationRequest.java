package com.example.opd.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequest {

    @NotBlank(message = "Blood pressure is required")
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "Blood pressure must be in format like 120/80")
    private String bloodPressure;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "90.0", message = "Temperature must be at least 90.0 °F")
    @DecimalMax(value = "110.0", message = "Temperature must be at most 110.0 °F")
    private BigDecimal temperature;

    @NotBlank(message = "Notes are required")
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
