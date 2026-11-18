package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Quote provided by a transporter for a specific lane")
public class LaneQuoteDto {

    @NotNull(message = "laneId is required")
    @Schema(description = "Lane ID", example = "1")
    private Long laneId;

    @NotNull(message = "quote is required")
    @Positive(message = "quote must be positive")
    @Schema(description = "Quoted price in INR", example = "5000")
    private Double quote;
}
