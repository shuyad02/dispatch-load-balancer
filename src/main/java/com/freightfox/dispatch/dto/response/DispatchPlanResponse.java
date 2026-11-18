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
@Schema(description = "Overall dispatch plan response")
public class DispatchPlanResponse {

    @Builder.Default
    @Schema(description = "List of vehicle dispatch plans")
    private List<VehicleDispatchPlanDto> dispatchPlan = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Schema(description = "Orders that could not be assigned due to capacity limits or vehicle constraints")
    private List<AssignedOrderDto> unassignedOrders = new ArrayList<>();
}
