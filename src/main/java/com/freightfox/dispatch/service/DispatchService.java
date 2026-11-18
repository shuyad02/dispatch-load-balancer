package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.request.OrdersBatchRequest;
import com.freightfox.dispatch.dto.request.VehiclesBatchRequest;
import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.dto.response.DispatchPlanResponse;

public interface DispatchService {

    ApiResponse saveOrders(OrdersBatchRequest request);

    ApiResponse saveVehicles(VehiclesBatchRequest request);

    DispatchPlanResponse getDispatchPlan();

    void clearAll();
}
