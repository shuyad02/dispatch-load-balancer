package com.freightfox.dispatch.transporter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transporter details and quoted lane rates")
public class TransporterDto {

    @NotNull(message = "Transporter id is required")
    @Schema(description = "Unique transporter ID", example = "1")
    private Long id;

    @NotBlank(message = "Transporter name is required")
    @Schema(description = "Transporter business name", example = "Transporter T1")
    private String name;

    @Builder.Default
    @Valid
    @Schema(description = "List of quotes across various lanes")
    private List<LaneQuoteDto> laneQuotes = new ArrayList<>();
}
