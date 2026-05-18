package org.sds.sdslocation.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.sds.sdslocation.model.DeviceLocation;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author samwel.wafula
 * Created on 5/18/2026
 * Time 10:35 AM
 */
@Table("user_locations")
@Getter
@Setter
@Builder
public class TblDeviceLocation {
    @Id
    @Column("device_id")
    private String deviceId;

    @Column("user_id")
    private String userId;

    @Column("lat")
    private BigDecimal lat;

    @Column("lon")
    private BigDecimal lon;

    @JsonIgnore
    @Column("location")
    private Point location; // Will use your geometry converter

    @Column("notification_token")
    private String notificationToken;

    @Column("device_type")
    private String deviceType;

    @Column("accuracy")
    private BigDecimal accuracy;

    @Column("status")
    private String status;

    @Column("metadata")
    private String metadata; // JSON string

    @Column("supported_services")
    private String[] supportedServices; // PostgreSQL array

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Convert to domain model for Redis
     */
    public DeviceLocation toDeviceLocation() {
        return DeviceLocation.builder()
                .deviceId(this.deviceId)
                .userId(this.userId)
                .latitude(this.lat != null ? this.lat.doubleValue() : null)
                .longitude(this.lon != null ? this.lon.doubleValue() : null)
                .notificationToken(this.notificationToken)
                .deviceType(this.deviceType)
                .accuracy(this.accuracy != null ? this.accuracy.doubleValue() : null)
                .status(this.status)
                .metadata(this.metadata)
                .supportedServices(this.supportedServices != null ? Arrays.asList(this.supportedServices) : null)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create from domain model
     */
    public static TblDeviceLocation fromDeviceLocation(DeviceLocation deviceLocation) {
        TblDeviceLocation builder = TblDeviceLocation.builder()
                .deviceId(deviceLocation.getDeviceId())
                .userId(deviceLocation.getUserId())
                .notificationToken(deviceLocation.getNotificationToken())
                .deviceType(deviceLocation.getDeviceType())
                .status(deviceLocation.getStatus())
                .metadata(deviceLocation.getMetadata())
                .createdAt(deviceLocation.getCreatedAt())
                .updatedAt(deviceLocation.getUpdatedAt()).build();

        // Handle coordinates
        if (deviceLocation.getLatitude() != null) {
            builder.setLat(BigDecimal.valueOf(deviceLocation.getLatitude()));
        }
        if (deviceLocation.getLongitude() != null) {
            builder.setLon(BigDecimal.valueOf(deviceLocation.getLongitude()));
        }
        if (deviceLocation.getAccuracy() != null) {
            builder.setAccuracy(BigDecimal.valueOf(deviceLocation.getAccuracy()));
        }

        // Handle supported services
        if (deviceLocation.getSupportedServices() != null && !deviceLocation.getSupportedServices().isEmpty()) {
            builder.setSupportedServices(deviceLocation.getSupportedServices().toArray(new String[0]));
        }

        return builder;
    }

}
