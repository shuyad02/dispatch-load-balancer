package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.dto.response.DispatchPlanResponse;
import com.freightfox.dispatch.dto.response.VehicleDispatchPlanDto;
import com.freightfox.dispatch.entity.OrderEntity;
import com.freightfox.dispatch.entity.Priority;
import com.freightfox.dispatch.entity.VehicleEntity;
import com.freightfox.dispatch.strategy.impl.PriorityNearestNeighborStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityNearestNeighborStrategyTest {

    private PriorityNearestNeighborStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PriorityNearestNeighborStrategy();
    }

    @Test
    @DisplayName("Empty orders returns empty dispatch plan for available vehicles")
    void emptyOrdersReturnsEmptyPlans() {
        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId("VEH001")
                .capacity(100.0)
                .currentLatitude(28.7041)
                .currentLongitude(77.1025)
                .currentAddress("Karol Bagh, Delhi")
                .build();

        DispatchPlanResponse response = strategy.optimizeDispatch(Collections.emptyList(), List.of(vehicle));

        assertNotNull(response);
        assertEquals(1, response.getDispatchPlan().size());
        VehicleDispatchPlanDto plan = response.getDispatchPlan().get(0);
        assertEquals("VEH001", plan.getVehicleId());
        assertEquals(0L, plan.getTotalLoad().longValue());
        assertEquals("0 km", plan.getTotalDistance());
        assertTrue(plan.getAssignedOrders().isEmpty());
    }

    @Test
    @DisplayName("Empty vehicles results in all orders being marked unassigned")
    void emptyVehiclesReturnsUnassignedOrders() {
        OrderEntity order = OrderEntity.builder()
                .orderId("ORD001")
                .latitude(28.6139)
                .longitude(77.2090)
                .address("Connaught Place, Delhi")
                .packageWeight(15.0)
                .priority(Priority.HIGH)
                .build();

        DispatchPlanResponse response = strategy.optimizeDispatch(List.of(order), Collections.emptyList());

        assertNotNull(response);
        assertTrue(response.getDispatchPlan().isEmpty());
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD001", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    @DisplayName("High priority orders are assigned before low priority orders")
    void highPriorityOrdersAssignedFirst() {
        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId("VEH001")
                .capacity(20.0)
                .currentLatitude(28.7041)
                .currentLongitude(77.1025)
                .currentAddress("Karol Bagh, Delhi")
                .build();

        // Low priority order placed first in the list
        OrderEntity lowOrder = OrderEntity.builder()
                .orderId("ORD_LOW")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh, Delhi")
                .packageWeight(15.0)
                .priority(Priority.LOW)
                .build();

        // High priority order placed second
        OrderEntity highOrder = OrderEntity.builder()
                .orderId("ORD_HIGH")
                .latitude(28.6139)
                .longitude(77.2090)
                .address("Connaught Place, Delhi")
                .packageWeight(15.0)
                .priority(Priority.HIGH)
                .build();

        // Total weight is 30.0, capacity is 20.0 -> Only one can fit!
        DispatchPlanResponse response = strategy.optimizeDispatch(List.of(lowOrder, highOrder), List.of(vehicle));

        assertEquals(1, response.getDispatchPlan().size());
        VehicleDispatchPlanDto plan = response.getDispatchPlan().get(0);

        // High priority must be assigned
        assertEquals(1, plan.getAssignedOrders().size());
        assertEquals("ORD_HIGH", plan.getAssignedOrders().get(0).getOrderId());

        // Low priority must be unassigned due to capacity limit
        assertEquals(1, response.getUnassignedOrders().size());
        assertEquals("ORD_LOW", response.getUnassignedOrders().get(0).getOrderId());
    }

    @Test
    @DisplayName("Vehicle capacity limit is strictly enforced")
    void vehicleCapacityIsEnforced() {
        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId("VEH001")
                .capacity(50.0)
                .currentLatitude(28.7041)
                .currentLongitude(77.1025)
                .currentAddress("Karol Bagh, Delhi")
                .build();

        OrderEntity order1 = OrderEntity.builder()
                .orderId("ORD001")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh, Delhi")
                .packageWeight(30.0)
                .priority(Priority.HIGH)
                .build();

        OrderEntity order2 = OrderEntity.builder()
                .orderId("ORD002")
                .latitude(28.7041)
                .longitude(77.1025)
                .address("Karol Bagh, Delhi")
                .packageWeight(30.0)
                .priority(Priority.HIGH)
                .build();

        // Both weigh 30 + 30 = 60, capacity is 50
        DispatchPlanResponse response = strategy.optimizeDispatch(List.of(order1, order2), List.of(vehicle));

        VehicleDispatchPlanDto plan = response.getDispatchPlan().get(0);
        assertEquals(30L, plan.getTotalLoad().longValue());
        assertEquals(1, plan.getAssignedOrders().size());
        assertEquals(1, response.getUnassignedOrders().size());
    }

    @Test
    @DisplayName("Nearest vehicle is chosen to minimize travel distance")
    void nearestVehicleIsChosen() {
        // Vehicle 1 at Bangalore
        VehicleEntity vBangalore = VehicleEntity.builder()
                .vehicleId("VEH_BLR")
                .capacity(100.0)
                .currentLatitude(12.9716)
                .currentLongitude(77.6413)
                .currentAddress("Indiranagar, Bangalore")
                .build();

        // Vehicle 2 at Delhi
        VehicleEntity vDelhi = VehicleEntity.builder()
                .vehicleId("VEH_DEL")
                .capacity(100.0)
                .currentLatitude(28.7041)
                .currentLongitude(77.1025)
                .currentAddress("Karol Bagh, Delhi")
                .build();

        // Order at MG Road, Bangalore (close to VEH_BLR)
        OrderEntity orderBlr = OrderEntity.builder()
                .orderId("ORD_BLR")
                .latitude(12.9716)
                .longitude(77.5946)
                .address("MG Road, Bangalore")
                .packageWeight(10.0)
                .priority(Priority.HIGH)
                .build();

        DispatchPlanResponse response = strategy.optimizeDispatch(List.of(orderBlr), List.of(vDelhi, vBangalore));

        VehicleDispatchPlanDto blrPlan = response.getDispatchPlan().stream()
                .filter(p -> p.getVehicleId().equals("VEH_BLR"))
                .findFirst().orElseThrow();

        assertEquals(1, blrPlan.getAssignedOrders().size());
        assertEquals("ORD_BLR", blrPlan.getAssignedOrders().get(0).getOrderId());
    }
}
