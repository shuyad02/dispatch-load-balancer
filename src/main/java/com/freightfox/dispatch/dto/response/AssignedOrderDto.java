package com.freightfox.dispatch.dto.response;

import com.freightfox.dispatch.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Assigned delivery order details")
public class AssignedOrderDto {

    @Schema(description = "Order ID", example = "ORD001")
    private String orderId;

    @Schema(description = "Drop latitude", example = "28.6139")
    private Double latitude;

    @Schema(description = "Drop longitude", example = "77.2090")
    private Double longitude;

    @Schema(description = "Drop address", example = "Connaught Place, Delhi, India")
    private String address;

    @Schema(description = "Weight in kg", example = "15.0")
    private Double packageWeight;

    @Schema(description = "Order priority", example = "HIGH")
    private Priority priority;
}
