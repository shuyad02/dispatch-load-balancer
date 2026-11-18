package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Freight lane representing a route between two cities")
public class LaneDto {

    @NotNull(message = "Lane id is required")
    @Schema(description = "Unique lane ID", example = "1")
    private Long id;

    @NotBlank(message = "Origin is required")
    @Schema(description = "Origin city/hub", example = "Mumbai")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Schema(description = "Destination city/hub", example = "Delhi")
    private String destination;
}
