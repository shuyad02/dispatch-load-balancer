package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.dto.response.DispatchPlanResponse;
import com.freightfox.dispatch.entity.OrderEntity;
import com.freightfox.dispatch.entity.VehicleEntity;

import java.util.List;

/**
 * Strategy interface for dispatch optimization.
 * Allows pluggable optimization algorithms (e.g. Greedy Priority Nearest Neighbor, Clarke-Wright, etc.)
 */
public interface DispatchOptimizationStrategy {

    /**
     * Executes dispatch optimization assigning orders to vehicles adhering to
     * priority, capacity, and distance constraints.
     *
     * @param orders List of orders to be dispatched
     * @param vehicles List of available fleet vehicles
     * @return DispatchPlanResponse containing the assigned routes and any unassigned orders
     */
    DispatchPlanResponse optimizeDispatch(List<OrderEntity> orders, List<VehicleEntity> vehicles);
}
