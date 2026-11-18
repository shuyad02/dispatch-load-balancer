package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Optimization request parameters")
public class AssignmentRequest {

    @NotNull(message = "maxTransporters is required")
    @Min(value = 1, message = "maxTransporters must be at least 1")
    @Schema(description = "Maximum number of transporters to utilize", example = "3")
    private Integer maxTransporters;
}
