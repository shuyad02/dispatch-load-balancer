package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.request.OrdersBatchRequest;
import com.freightfox.dispatch.dto.request.VehiclesBatchRequest;
import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.dto.response.DispatchPlanResponse;
import com.freightfox.dispatch.dto.response.ErrorResponse;
import com.freightfox.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
@Tag(name = "Dispatch Load Balancer", description = "Fleet allocation and route optimization APIs")
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping("/orders")
    @Operation(summary = "Input Delivery Orders", description = "Accepts and persists a batch of delivery orders with drop locations, weights, and priorities.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Delivery orders accepted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> inputOrders(@Valid @RequestBody OrdersBatchRequest request) {
        ApiResponse response = dispatchService.saveOrders(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehicles")
    @Operation(summary = "Input Fleet Details", description = "Accepts and persists a batch of fleet vehicles with capacity and starting coordinates.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vehicle details accepted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> inputVehicles(@Valid @RequestBody VehiclesBatchRequest request) {
        ApiResponse response = dispatchService.saveVehicles(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    @Operation(summary = "Retrieve Dispatch Plan", description = "Calculates and returns the optimized dispatch plan adhering to capacity constraints, priority ordering, and minimum travel distance.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Optimized dispatch plan generated successfully",
                    content = @Content(schema = @Schema(implementation = DispatchPlanResponse.class))
            )
    })
    public ResponseEntity<DispatchPlanResponse> getDispatchPlan() {
        DispatchPlanResponse plan = dispatchService.getDispatchPlan();
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/reset")
    @Operation(summary = "Reset Orders and Vehicles", description = "Clears all currently stored orders and vehicles from the database.")
    public ResponseEntity<ApiResponse> resetData() {
        dispatchService.clearAll();
        return ResponseEntity.ok(ApiResponse.success("All orders and fleet vehicles have been cleared."));
    }
}
