package org.sds.sdslocation.controller;

import com.sds.integration.commons.model.AbstractBaseApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.model.DeviceLocation;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.model.request.NotifyNearbyRequest;
import org.sds.sdslocation.service.DeviceLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author samwel.wafula
 * Created on 5/18/2026
 * Time 1:10 PM
 */
@RestController
@RequestMapping("/device-location")
@Slf4j
public class DeviceLocationController {

    private final DeviceLocationService deviceLocationService;

    public DeviceLocationController(DeviceLocationService deviceLocationService) {
        this.deviceLocationService = deviceLocationService;
    }

    /**
     * Update device location
     */
    @PostMapping("/update")
    public ResponseEntity<AbstractBaseApiResponse<DeviceLocation>> updateLocation(
            @RequestBody DeviceLocation deviceLocation) {

        DeviceLocation updated = deviceLocationService.updateDeviceLocation(deviceLocation);

        return ResponseEntity.ok(new ApiResponse<DeviceLocation>().success(
                "200",
                "Location updated successfully",
                updated
        ));
    }

    /**
     * Notify nearby specialists for a service
     */
    @PostMapping("/notify-nearby/service")
    public ResponseEntity<AbstractBaseApiResponse<String>> notifyNearbyService(@Valid @RequestBody NotifyNearbyRequest request) {

        deviceLocationService.pushNotificationToNearbyUsers(request);

        return ResponseEntity.ok(new ApiResponse<String>().success(
                "200",
                "Notification process started",
                "Nearby " + request.getRequestDetails() + " specialists will be notified"
        ));
    }

    /**
     * Get user location
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AbstractBaseApiResponse<DeviceLocation>> getUserLocation(
            @PathVariable String userId) {

        Optional<DeviceLocation> location = deviceLocationService.getDeviceLocationByUserId(userId);

        return location.map(deviceLocation -> ResponseEntity.ok(new ApiResponse<DeviceLocation>().success(
                "200",
                "Location found",
                deviceLocation
        ))).orElseGet(() -> ResponseEntity.status(404)
                .body(new ApiResponse<DeviceLocation>().error(
                        "404",
                        "Location not found",
                        "No location found for user: " + userId
                )));
    }

    /**
     * Update user status
     */
    @PutMapping("/user/{userId}/status/{status}")
    public ResponseEntity<AbstractBaseApiResponse<String>> updateUserStatus(
            @PathVariable String userId,
            @PathVariable DeviceStatus status) {

        boolean updated = deviceLocationService.updateUserStatus(userId, status);

        if (updated) {
            return ResponseEntity.ok(new ApiResponse<String>().success(
                    "200",
                    "Status updated successfully",
                    "User status updated to: " + status
            ));
        } else {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<String>().error(
                            "404",
                            "Update failed",
                            "User not found or status unchanged"
                    ));
        }
    }

    /**
     * Get nearby available devices by distance and service type
     */
    @GetMapping("/nearby")
    public ResponseEntity<AbstractBaseApiResponse<List<DeviceLocation>>> getNearbyDevices(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "1000") Double radiusMeters,
            @RequestParam(required = false) List<String> serviceTypes,
            @RequestParam(required = false, defaultValue = "20") Integer maxResults) {

        List<DeviceLocation> nearbyDevices;
        if (serviceTypes != null && !serviceTypes.isEmpty()) {
            nearbyDevices = deviceLocationService.findNearbyUsersWithServiceConsideration(
                    latitude,
                    longitude,
                    serviceTypes,
                    radiusMeters,
                    maxResults
            );
        }else {
            nearbyDevices = deviceLocationService.findNearbyUsers(
                    latitude,
                    longitude,
                    radiusMeters,
                    maxResults
            );
        }

        String message = String.format("Found %d devices near you", nearbyDevices.size());

        return ResponseEntity.ok(new ApiResponse<List<DeviceLocation>>().success(
                "200",
                message,
                nearbyDevices
        ));
    }
}
