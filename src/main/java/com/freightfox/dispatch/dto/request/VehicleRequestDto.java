package com.freightfox.dispatch.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Fleet vehicle details")
public class VehicleRequestDto {

    @NotBlank(message = "vehicleId is required and cannot be blank")
    @Schema(description = "Unique identifier of the vehicle", example = "VEH001")
    private String vehicleId;

    @NotNull(message = "capacity is required")
    @Positive(message = "capacity must be greater than 0")
    @Schema(description = "Maximum payload capacity in kg", example = "100.0")
    private Double capacity;

    @NotNull(message = "currentLatitude is required")
    @DecimalMin(value = "-90.0", message = "currentLatitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "currentLatitude must be between -90 and 90")
    @Schema(description = "Vehicle current latitude", example = "28.7041")
    private Double currentLatitude;

    @NotNull(message = "currentLongitude is required")
    @DecimalMin(value = "-180.0", message = "currentLongitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "currentLongitude must be between -180 and 180")
    @Schema(description = "Vehicle current longitude", example = "77.1025")
    private Double currentLongitude;

    @NotBlank(message = "currentAddress is required and cannot be blank")
    @Schema(description = "Human-readable address of vehicle starting position", example = "Karol Bagh, Delhi, India")
    private String currentAddress;
}
