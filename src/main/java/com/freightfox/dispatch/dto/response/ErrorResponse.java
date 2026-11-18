package com.freightfox.dispatch.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "Status indicator", example = "error")
    private String status;

    @Schema(description = "Error description message", example = "Validation failed for one or more fields")
    private String message;

    @Schema(description = "Timestamp when error occurred")
    private LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Specific validation or domain error details")
    private List<String> details;

    public static ErrorResponse of(String message, List<String> details) {
        return ErrorResponse.builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }

    public static ErrorResponse of(String message) {
        return of(message, null);
    }
}
