package com.freightfox.dispatch.transporter;

import com.freightfox.dispatch.transporter.dto.AssignmentRequest;
import com.freightfox.dispatch.transporter.dto.AssignmentResponse;
import com.freightfox.dispatch.transporter.service.impl.TransporterAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransporterAssignmentServiceTest {

    private TransporterAssignmentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TransporterAssignmentServiceImpl();
    }

    @Test
    @DisplayName("Test Case 1 optimization adheres to constraints")
    void testCase1Optimization() {
        service.loadTestCase1();
        AssignmentResponse response = service.getOptimizedAssignment(new AssignmentRequest(3));

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertTrue(response.getSelectedTransporters().size() <= 3);
        assertEquals(5, response.getAssignments().size());
        assertTrue(response.getTotalCost().doubleValue() > 0);
    }

    @Test
    @DisplayName("Test Case 2 optimization covers all 5 lanes")
    void testCase2Optimization() {
        service.loadTestCase2();
        AssignmentResponse response = service.getOptimizedAssignment(new AssignmentRequest(3));

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals(5, response.getAssignments().size());
    }

    @Test
    @DisplayName("Throws exception if maxTransporters is insufficient to cover all lanes")
    void insufficientTransportersThrowsException() {
        service.loadTestCase2(); // Needs at least 3 transporters to cover all 5 lanes (Z covers only lane 5)
        assertThrows(IllegalArgumentException.class, () -> service.getOptimizedAssignment(new AssignmentRequest(1)));
    }
}
