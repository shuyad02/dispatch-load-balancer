package com.freightfox.dispatch.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Batch of fleet vehicles to be registered")
public class VehiclesBatchRequest {

    @NotEmpty(message = "vehicles list cannot be empty")
    @Valid
    @Schema(description = "List of fleet vehicles")
    private List<VehicleRequestDto> vehicles;
}
