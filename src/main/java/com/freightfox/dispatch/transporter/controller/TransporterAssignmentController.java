package com.freightfox.dispatch.transporter.controller;

import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.dto.response.ErrorResponse;
import com.freightfox.dispatch.transporter.dto.AssignmentRequest;
import com.freightfox.dispatch.transporter.dto.AssignmentResponse;
import com.freightfox.dispatch.transporter.dto.TransportersInputRequest;
import com.freightfox.dispatch.transporter.service.TransporterAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transporters")
@RequiredArgsConstructor
@Tag(name = "Transporter Assignment on Lanes", description = "Freight tendering and carrier lane allocation APIs")
public class TransporterAssignmentController {

    private final TransporterAssignmentService transporterService;

    @PostMapping("/input")
    @Operation(summary = "Submit Input Data", description = "Allows users to input data for lanes and transporter quotes.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Input data saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> submitInput(@Valid @RequestBody TransportersInputRequest request) {
        ApiResponse response = transporterService.saveInputData(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assignment")
    @Operation(summary = "Get Optimized Results", description = "Provides optimized transporter assignments based on cost minimization, lane coverage, and maximum transporter count.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Optimized assignments calculated successfully",
                    content = @Content(schema = @Schema(implementation = AssignmentResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cannot satisfy constraints",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AssignmentResponse> getAssignments(@Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = transporterService.getOptimizedAssignment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    @Operation(summary = "Get Current Data", description = "Retrieves currently loaded lanes and transporter quotes.")
    public ResponseEntity<TransportersInputRequest> getData() {
        return ResponseEntity.ok(transporterService.getCurrentData());
    }

    @PostMapping("/sample/testcase1")
    @Operation(summary = "Load Test Case 1 Sample", description = "Loads the sample dataset from Test Case 1 in the PDF (5 lanes, 7 transporters).")
    public ResponseEntity<ApiResponse> loadTestCase1() {
        ApiResponse response = transporterService.loadTestCase1();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sample/testcase2")
    @Operation(summary = "Load Test Case 2 Sample", description = "Loads the sample dataset from Test Case 2 in the PDF (5 lanes, 4 transporters).")
    public ResponseEntity<ApiResponse> loadTestCase2() {
        ApiResponse response = transporterService.loadTestCase2();
        return ResponseEntity.ok(response);
    }
}
