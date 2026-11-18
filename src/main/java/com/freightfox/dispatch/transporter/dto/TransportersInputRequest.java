package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Input data containing lanes and transporter quotes")
public class TransportersInputRequest {

    @NotEmpty(message = "lanes list cannot be empty")
    @Valid
    @Builder.Default
    @Schema(description = "List of freight lanes")
    private List<LaneDto> lanes = new ArrayList<>();

    @NotEmpty(message = "transporters list cannot be empty")
    @Valid
    @Builder.Default
    @Schema(description = "List of transporters and quotes")
    private List<TransporterDto> transporters = new ArrayList<>();
}
