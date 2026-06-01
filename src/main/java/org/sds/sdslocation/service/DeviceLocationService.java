package org.sds.sdslocation.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.sds.sdslocation.model.DeviceLocation;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.model.request.NotifyNearbyRequest;
import org.sds.sdslocation.repository.DataRepository;
import org.sds.sdslocation.repository.TblDeviceLocation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Joseph Kibe
 * Created on 5/18/26
 */
@Service
@Slf4j
public class DeviceLocationService {

    private final DataRepository deviceLocationRepository;

    private static final int LOCATION_TIMEOUT_MINUTES = 1000;
    private static final int MAX_RADIUS_IN_KILOMETER = 5;

    public DeviceLocationService(DataRepository deviceLocationRepository) {
        this.deviceLocationRepository = deviceLocationRepository;
    }

    /**
     * Update device location in both Redis and PostgreSQL
     */
    @Transactional
    public DeviceLocation updateDeviceLocation(DeviceLocation deviceLocation) {
        try {
            if (deviceLocation.getDeviceType() == null) {
                throw new IllegalArgumentException("Device type is required");
            }
            if (deviceLocation.getStatus() == null) {
                throw new IllegalArgumentException("Device status is required");
            }


            // 1. Save to PostgresSQL first (source of truth)
            TblDeviceLocation saved = deviceLocationRepository.saveDeviceLocation(
                    deviceLocation.getDeviceId(),
                    deviceLocation.getUserId(),
                    deviceLocation.getLat(),
                    deviceLocation.getLon(),
                    deviceLocation.getNotificationToken(),
                    deviceLocation.getDeviceType().name(),
                    deviceLocation.getAccuracy(),
                    deviceLocation.getStatus().name(),
                    deviceLocation.getMetadata(),
                    deviceLocation.getSupportedServices() != null ?
                            deviceLocation.getSupportedServices().toArray(new String[0]) : null
            );

            log.info("Updated device location for user: {} in both PostgreSQL and Redis",
                    deviceLocation.getUserId());

            return saved.toDeviceLocation();
        } catch (Exception e) {
            log.error("Error updating device location for user: {}",
                    deviceLocation.getUserId(), e);
            throw new SdsLocationException("Failed to update device location", e);
        }
    }

    /**
     * Get device location by userId
     */
    public Optional<DeviceLocation> getDeviceLocationByUserId(String userId) {
        TblDeviceLocation tblDeviceLocation = deviceLocationRepository.findDeviceLocationByUserId(userId);
        if (tblDeviceLocation == null) {
            return Optional.empty();
        }
        return Optional.of(tblDeviceLocation.toDeviceLocation());

    }

    /**
     * fallback for nearby users when Redis fails
     */
    public List<DeviceLocation> findNearbyUsersWithServiceConsideration(Double latitude, Double longitude,
                                                                        List<String> serviceTypes, Double radiusMeters,
                                                                        Integer maxResults) {
        try {

            List<TblDeviceLocation> results = deviceLocationRepository.findNearbyUsersWithService(
                    latitude,
                    longitude,
                    radiusMeters != null ? radiusMeters : 5000.0,
                    maxResults != null ? maxResults : 20,
                    LOCATION_TIMEOUT_MINUTES
            );

            return results.stream()
                    .filter(deviceLocation -> {
                        String[] supportedServices = deviceLocation.getSupportedServices();
                        return Arrays.stream(supportedServices)
                                .anyMatch(serviceTypes::contains);
                    })
                    .map(TblDeviceLocation::toDeviceLocation)
                    .toList();
        } catch (Exception e) {
            log.error("Error finding nearby : {}", e.getMessage(), e);
            return List.of();
        }
    }
    /**
     * fallback for nearby users when Redis fails
     */
    public List<DeviceLocation> findNearbyUsers(Double latitude, Double longitude, Double radiusMeters,
                                                                        Integer maxResults) {
        try {

            List<TblDeviceLocation> results = deviceLocationRepository.findNearbyUsersWithService(
                    latitude,
                    longitude,
                    radiusMeters != null ? radiusMeters : 5000.0,
                    maxResults != null ? maxResults : 20,
                    LOCATION_TIMEOUT_MINUTES
            );

            return results.stream()
                    .map(TblDeviceLocation::toDeviceLocation)
                    .toList();
        } catch (Exception e) {
            log.error("Error finding nearby : {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Update user status
     */
    @Transactional
    public boolean updateUserStatus(String userId, DeviceStatus status) {
        try {
            boolean updated = deviceLocationRepository.updateUserStatus(userId, status);

            if (updated) {
                // Update Redis as well
                Optional<DeviceLocation> deviceLocation = getDeviceLocationByUserId(userId);
                deviceLocation.ifPresent(updated_device -> updated_device.setStatus(status));
            }

            return updated;
        } catch (Exception e) {
            log.error("Error updating user status for userId: {}", userId, e);
            return false;
        }
    }

    /**
     * Find nearby users and initiate a notification process for service requests
     */
    @Transactional
    public void pushNotificationToNearbyUsers(NotifyNearbyRequest request) {
        try {

            // Set the default radius if not provided
            Double searchRadius = request.getRadius() != null ? request.getRadius() : MAX_RADIUS_IN_KILOMETER * 1000; // 5km default

            // Find nearby available users with the required service
            List<DeviceLocation> nearbyUsers = findNearbyUsersWithServiceConsideration(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getServiceTypes(),
                    searchRadius,
                    50);

            log.info("Found {} nearby users for service: {}", nearbyUsers.size(), request.getServiceTypes());

            if (nearbyUsers.isEmpty()) {
                log.warn("No nearby users found for service: {} within radius: {}m",
                        request.getServiceTypes(), searchRadius);
                return;
            }

            // Process notifications for each nearby user
            nearbyUsers.forEach(user -> {
                try {
                    // Here you would typically send push notifications or save notification records
                    // For now, we'll log the notification attempt
                    log.info("Notifying user: {} (deviceId: {}) for service request: {}",
                            user.getUserId(), user.getDeviceId(), request.getRequestId());

                    // - Implement actual notification sending logic
                    // - Send push notification using FCM/APNS
                    // - Save notification record to database
                    // - Update user notification stats

                } catch (Exception e) {
                    log.error("Failed to notify user: {} for request: {}",
                            user.getUserId(), request.getRequestId(), e);
                }
            });

            log.info("Notification process completed for request: {} - notified {} users",
                    request.getRequestId(), nearbyUsers.size());
        } catch (Exception e) {
            log.error("Error in notification process for request: {} and service: {}",
                    request.getRequestId(), request.getServiceTypes(), e);
            throw new SdsLocationException("Failed to process nearby user notifications", e);
        }
    }

    /**
     * Clean up stale locations
     */
    @Transactional
    public int cleanupStaleLocations(int timeoutMinutes) {
        return deviceLocationRepository.deleteStaleDeviceLocations(timeoutMinutes);
    }
}
