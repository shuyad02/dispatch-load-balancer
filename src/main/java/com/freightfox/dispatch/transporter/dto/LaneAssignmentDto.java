package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Assigned transporter for a lane")
public class LaneAssignmentDto {

    @Schema(description = "Lane ID", example = "1")
    private Long laneId;

    @Schema(description = "Transporter ID assigned to this lane", example = "1")
    private Long transporterId;
}
