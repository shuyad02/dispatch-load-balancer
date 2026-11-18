package com.freightfox.dispatch.dto.request;

import com.freightfox.dispatch.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Delivery order details")
public class OrderRequestDto {

    @NotBlank(message = "orderId is required and cannot be blank")
    @Schema(description = "Unique identifier of the order", example = "ORD001")
    private String orderId;

    @NotNull(message = "latitude is required")
    @DecimalMin(value = "-90.0", message = "latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "latitude must be between -90 and 90")
    @Schema(description = "Drop point latitude", example = "28.6139")
    private Double latitude;

    @NotNull(message = "longitude is required")
    @DecimalMin(value = "-180.0", message = "longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "longitude must be between -180 and 180")
    @Schema(description = "Drop point longitude", example = "77.2090")
    private Double longitude;

    @NotBlank(message = "address is required and cannot be blank")
    @Schema(description = "Human-readable address of the drop location", example = "Connaught Place, Delhi, India")
    private String address;

    @NotNull(message = "packageWeight is required")
    @Positive(message = "packageWeight must be greater than 0")
    @Schema(description = "Weight of the delivery package in kg", example = "15.0")
    private Double packageWeight;

    @NotNull(message = "priority is required (HIGH, MEDIUM, LOW)")
    @Schema(description = "Order priority", example = "HIGH")
    private Priority priority;
}
