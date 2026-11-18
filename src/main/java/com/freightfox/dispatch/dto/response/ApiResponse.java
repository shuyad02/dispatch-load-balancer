package com.freightfox.dispatch.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard operation acknowledgement response")
public class ApiResponse {

    @Schema(description = "Acknowledgement message", example = "Delivery orders accepted.")
    private String message;

    @Schema(description = "Status of the operation", example = "success")
    private String status;

    public static ApiResponse success(String message) {
        return ApiResponse.builder()
                .status("success")
                .message(message)
                .build();
    }
}
