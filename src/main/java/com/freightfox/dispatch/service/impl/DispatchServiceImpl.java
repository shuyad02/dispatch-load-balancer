package com.freightfox.dispatch.service.impl;

import com.freightfox.dispatch.dto.request.OrderRequestDto;
import com.freightfox.dispatch.dto.request.OrdersBatchRequest;
import com.freightfox.dispatch.dto.request.VehicleRequestDto;
import com.freightfox.dispatch.dto.request.VehiclesBatchRequest;
import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.dto.response.DispatchPlanResponse;
import com.freightfox.dispatch.entity.OrderEntity;
import com.freightfox.dispatch.entity.VehicleEntity;
import com.freightfox.dispatch.exception.InvalidCoordinatesException;
import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.service.DispatchService;
import com.freightfox.dispatch.strategy.DispatchOptimizationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchOptimizationStrategy optimizationStrategy;

    @Override
    @Transactional
    public ApiResponse saveOrders(OrdersBatchRequest request) {
        log.info("Receiving batch of {} orders", request.getOrders() != null ? request.getOrders().size() : 0);

        List<OrderEntity> entities = new ArrayList<>();
        for (OrderRequestDto dto : request.getOrders()) {
            validateCoordinates(dto.getLatitude(), dto.getLongitude(), "Order " + dto.getOrderId());

            OrderEntity entity = OrderEntity.builder()
                    .orderId(dto.getOrderId().trim())
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .address(dto.getAddress().trim())
                    .packageWeight(dto.getPackageWeight())
                    .priority(dto.getPriority())
                    .build();
            entities.add(entity);
        }

        orderRepository.saveAll(entities);
        log.info("Successfully persisted/upserted {} orders", entities.size());

        return ApiResponse.success("Delivery orders accepted.");
    }

    @Override
    @Transactional
    public ApiResponse saveVehicles(VehiclesBatchRequest request) {
        log.info("Receiving batch of {} vehicles", request.getVehicles() != null ? request.getVehicles().size() : 0);

        List<VehicleEntity> entities = new ArrayList<>();
        for (VehicleRequestDto dto : request.getVehicles()) {
            validateCoordinates(dto.getCurrentLatitude(), dto.getCurrentLongitude(), "Vehicle " + dto.getVehicleId());

            VehicleEntity entity = VehicleEntity.builder()
                    .vehicleId(dto.getVehicleId().trim())
                    .capacity(dto.getCapacity())
                    .currentLatitude(dto.getCurrentLatitude())
                    .currentLongitude(dto.getCurrentLongitude())
                    .currentAddress(dto.getCurrentAddress().trim())
                    .build();
            entities.add(entity);
        }

        vehicleRepository.saveAll(entities);
        log.info("Successfully persisted/upserted {} vehicles", entities.size());

        return ApiResponse.success("Vehicle details accepted.");
    }

    @Override
    @Transactional(readOnly = true)
    public DispatchPlanResponse getDispatchPlan() {
        List<OrderEntity> orders = orderRepository.findAll();
        List<VehicleEntity> vehicles = vehicleRepository.findAll();

        log.info("Calculating dispatch plan for {} orders across {} fleet vehicles", orders.size(), vehicles.size());
        return optimizationStrategy.optimizeDispatch(orders, vehicles);
    }

    @Override
    @Transactional
    public void clearAll() {
        orderRepository.deleteAll();
        vehicleRepository.deleteAll();
        log.info("Cleared all orders and vehicles from database");
    }

    private void validateCoordinates(Double lat, Double lon, String context) {
        if (lat == null || lat < -90.0 || lat > 90.0) {
            throw new InvalidCoordinatesException(context + " has invalid latitude: " + lat + ". Must be between -90 and 90.");
        }
        if (lon == null || lon < -180.0 || lon > 180.0) {
            throw new InvalidCoordinatesException(context + " has invalid longitude: " + lon + ". Must be between -180 and 180.");
        }
    }
}
