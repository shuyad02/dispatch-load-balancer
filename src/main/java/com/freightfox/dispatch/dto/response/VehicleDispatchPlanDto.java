package com.freightfox.dispatch.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dispatch plan per vehicle")
public class VehicleDispatchPlanDto {

    @Schema(description = "Vehicle ID", example = "VEH001")
    private String vehicleId;

    @Schema(description = "Total assigned load in kg", example = "10")
    private Number totalLoad;

    @Schema(description = "Total travel distance formatted with unit", example = "5 km")
    private String totalDistance;

    @Builder.Default
    @Schema(description = "List of assigned orders in visit sequence")
    private List<AssignedOrderDto> assignedOrders = new ArrayList<>();
}
