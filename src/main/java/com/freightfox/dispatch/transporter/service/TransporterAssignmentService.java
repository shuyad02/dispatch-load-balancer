package com.freightfox.dispatch.transporter.service;

import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.transporter.dto.AssignmentRequest;
import com.freightfox.dispatch.transporter.dto.AssignmentResponse;
import com.freightfox.dispatch.transporter.dto.TransportersInputRequest;

public interface TransporterAssignmentService {

    ApiResponse saveInputData(TransportersInputRequest request);

    AssignmentResponse getOptimizedAssignment(AssignmentRequest request);

    TransportersInputRequest getCurrentData();

    ApiResponse loadTestCase1();

    ApiResponse loadTestCase2();
}
