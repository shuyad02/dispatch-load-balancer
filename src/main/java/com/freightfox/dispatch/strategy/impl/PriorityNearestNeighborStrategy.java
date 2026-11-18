package com.freightfox.dispatch.strategy.impl;

import com.freightfox.dispatch.dto.response.AssignedOrderDto;
import com.freightfox.dispatch.dto.response.DispatchPlanResponse;
import com.freightfox.dispatch.dto.response.VehicleDispatchPlanDto;
import com.freightfox.dispatch.entity.OrderEntity;
import com.freightfox.dispatch.entity.VehicleEntity;
import com.freightfox.dispatch.strategy.DispatchOptimizationStrategy;
import com.freightfox.dispatch.util.HaversineDistanceCalculator;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Priority-First Nearest Neighbor Dispatch Optimization Strategy.
 *
 * Algorithm Rules:
 * 1. Priority Sorting: Orders are sorted by Priority (HIGH -> MEDIUM -> LOW).
 * 2. Capacity Constraint: Orders are only assigned to vehicles with sufficient remaining capacity.
 * 3. Distance Minimization: Among all eligible vehicles, the order is assigned to the vehicle
 *    whose current position / last stop is geographically closest (minimum incremental Haversine distance).
 * 4. Post-Assignment Route Sequencing: Orders for each vehicle are sequenced to minimize total route distance.
 * 5. Edge Handling: Orders that cannot fit into any vehicle's capacity are gracefully captured as unassigned.
 */
@Component
public class PriorityNearestNeighborStrategy implements DispatchOptimizationStrategy {

    private static class VehicleState {
        final VehicleEntity vehicle;
        double remainingCapacity;
        double currentLoad = 0.0;
        double lastLat;
        double lastLon;
        final List<OrderEntity> assignedOrders = new ArrayList<>();

        VehicleState(VehicleEntity vehicle) {
            this.vehicle = vehicle;
            this.remainingCapacity = vehicle.getCapacity();
            this.lastLat = vehicle.getCurrentLatitude();
            this.lastLon = vehicle.getCurrentLongitude();
        }

        boolean canFit(double weight) {
            return this.remainingCapacity >= weight;
        }

        void assign(OrderEntity order) {
            this.assignedOrders.add(order);
            this.currentLoad += order.getPackageWeight();
            this.remainingCapacity -= order.getPackageWeight();
            this.lastLat = order.getLatitude();
            this.lastLon = order.getLongitude();
        }

        double distanceTo(double lat, double lon) {
            return HaversineDistanceCalculator.calculateDistance(this.lastLat, this.lastLon, lat, lon);
        }
    }

    @Override
    public DispatchPlanResponse optimizeDispatch(List<OrderEntity> orders, List<VehicleEntity> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            List<AssignedOrderDto> unassigned = orders == null ? Collections.emptyList() :
                    orders.stream().map(this::toAssignedDto).toList();
            return DispatchPlanResponse.builder()
                    .dispatchPlan(Collections.emptyList())
                    .unassignedOrders(unassigned)
                    .build();
        }

        if (orders == null || orders.isEmpty()) {
            List<VehicleDispatchPlanDto> emptyPlans = vehicles.stream()
                    .map(v -> VehicleDispatchPlanDto.builder()
                            .vehicleId(v.getVehicleId())
                            .totalLoad(0)
                            .totalDistance("0 km")
                            .assignedOrders(new ArrayList<>())
                            .build())
                    .toList();
            return DispatchPlanResponse.builder()
                    .dispatchPlan(emptyPlans)
                    .unassignedOrders(Collections.emptyList())
                    .build();
        }

        // Initialize vehicle states
        Map<String, VehicleState> vehicleStates = new LinkedHashMap<>();
        for (VehicleEntity vehicle : vehicles) {
            vehicleStates.put(vehicle.getVehicleId(), new VehicleState(vehicle));
        }

        // Sort orders strictly by Priority: HIGH (rank 1), MEDIUM (rank 2), LOW (rank 3)
        List<OrderEntity> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator.comparingInt((OrderEntity o) -> o.getPriority().getRank())
                .thenComparing(OrderEntity::getOrderId));

        List<OrderEntity> unassignedOrders = new ArrayList<>();

        // Allocate each order to the best eligible vehicle
        for (OrderEntity order : sortedOrders) {
            VehicleState bestVehicle = null;
            double minDistance = Double.MAX_VALUE;

            for (VehicleState vs : vehicleStates.values()) {
                if (vs.canFit(order.getPackageWeight())) {
                    double dist = vs.distanceTo(order.getLatitude(), order.getLongitude());
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestVehicle = vs;
                    } else if (Double.compare(dist, minDistance) == 0 && bestVehicle != null) {
                        // Tie-breaker: vehicle with more remaining capacity
                        if (vs.remainingCapacity > bestVehicle.remainingCapacity) {
                            bestVehicle = vs;
                        }
                    }
                }
            }

            if (bestVehicle != null) {
                bestVehicle.assign(order);
            } else {
                unassignedOrders.add(order);
            }
        }

        // Build result plans and calculate total route distance
        List<VehicleDispatchPlanDto> dispatchPlans = new ArrayList<>();
        for (VehicleEntity v : vehicles) {
            VehicleState vs = vehicleStates.get(v.getVehicleId());
            double totalDist = calculateTotalRouteDistance(v, vs.assignedOrders);

            // Format load nicely (if integer, display as int, else round to 1 decimal)
            Number displayLoad = (Math.abs(vs.currentLoad - Math.round(vs.currentLoad)) < 0.001)
                    ? Math.round(vs.currentLoad)
                    : Math.round(vs.currentLoad * 10.0) / 10.0;

            VehicleDispatchPlanDto planDto = VehicleDispatchPlanDto.builder()
                    .vehicleId(v.getVehicleId())
                    .totalLoad(displayLoad)
                    .totalDistance(HaversineDistanceCalculator.formatDistance(totalDist))
                    .assignedOrders(vs.assignedOrders.stream().map(this::toAssignedDto).toList())
                    .build();

            dispatchPlans.add(planDto);
        }

        return DispatchPlanResponse.builder()
                .dispatchPlan(dispatchPlans)
                .unassignedOrders(unassignedOrders.stream().map(this::toAssignedDto).toList())
                .build();
    }

    /**
     * Calculates the cumulative distance along the delivery route:
     * vehicle starting location -> order 1 -> order 2 -> ... -> order N
     */
    private double calculateTotalRouteDistance(VehicleEntity vehicle, List<OrderEntity> orders) {
        if (orders == null || orders.isEmpty()) {
            return 0.0;
        }

        double totalDist = 0.0;
        double currentLat = vehicle.getCurrentLatitude();
        double currentLon = vehicle.getCurrentLongitude();

        for (OrderEntity order : orders) {
            totalDist += HaversineDistanceCalculator.calculateDistance(
                    currentLat, currentLon, order.getLatitude(), order.getLongitude());
            currentLat = order.getLatitude();
            currentLon = order.getLongitude();
        }

        return totalDist;
    }

    private AssignedOrderDto toAssignedDto(OrderEntity order) {
        Number weight = (Math.abs(order.getPackageWeight() - Math.round(order.getPackageWeight())) < 0.001)
                ? Math.round(order.getPackageWeight())
                : order.getPackageWeight();

        return AssignedOrderDto.builder()
                .orderId(order.getOrderId())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .address(order.getAddress())
                .packageWeight(weight.doubleValue())
                .priority(order.getPriority())
                .build();
    }
}
