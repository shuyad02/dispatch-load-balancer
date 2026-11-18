package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {

    Optional<VehicleEntity> findByVehicleId(String vehicleId);
}
