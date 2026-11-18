package com.freightfox.dispatch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fleet_vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

    @Id
    @Column(name = "vehicle_id", nullable = false, unique = true)
    private String vehicleId;

    @Column(nullable = false)
    private Double capacity;

    @Column(name = "current_latitude", nullable = false)
    private Double currentLatitude;

    @Column(name = "current_longitude", nullable = false)
    private Double currentLongitude;

    @Column(name = "current_address", nullable = false, length = 500)
    private String currentAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
