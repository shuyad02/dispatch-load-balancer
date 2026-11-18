package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Optimized transporter assignment result")
public class AssignmentResponse {

    @Schema(description = "Response status", example = "success")
    private String status;

    @Schema(description = "Total cost of assigned lanes", example = "29000")
    private Number totalCost;

    @Builder.Default
    @Schema(description = "Per-lane transporter assignments")
    private List<LaneAssignmentDto> assignments = new ArrayList<>();

    @Builder.Default
    @Schema(description = "List of selected transporter IDs")
    private List<Long> selectedTransporters = new ArrayList<>();
}
