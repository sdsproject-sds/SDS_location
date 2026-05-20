package org.sds.sdslocation.controller;

import com.sds.integration.commons.model.AbstractBaseApiResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sds.sdslocation.model.DeviceLocation;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.model.request.NotifyNearbyRequest;
import org.sds.sdslocation.service.DeviceLocationService;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceLocationControllerTest {

    @Mock
    private DeviceLocationService deviceLocationService;

    @InjectMocks
    private DeviceLocationController deviceLocationController;

    public DeviceLocationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateLocation_Success() {
        // Arrange
        DeviceLocation deviceLocation = DeviceLocation.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        when(deviceLocationService.updateDeviceLocation(deviceLocation)).thenReturn(deviceLocation);

        // Act
        ResponseEntity<AbstractBaseApiResponse<DeviceLocation>> response =
                deviceLocationController.updateLocation(deviceLocation);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(deviceLocation);

        verify(deviceLocationService, times(1)).updateDeviceLocation(deviceLocation);
    }

    @Test
    void testNotifyNearbyService_Success() {
        // Arrange
        NotifyNearbyRequest request = NotifyNearbyRequest.builder()
                .latitude(12.34)
                .longitude(56.78)
                .requestId("testRequestId")
                .requestDetails("Request details")
                .radius(100.0)
                .build();

        // Act
        ResponseEntity<AbstractBaseApiResponse<String>> response =
                deviceLocationController.notifyNearbyService(request);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo("Nearby Request details specialists will be notified");

        verify(deviceLocationService, times(1)).pushNotificationToNearbyUsers(request);
    }

    @Test
    void testNotifyNearbyService_InvalidRequest() {
        // Arrange
        NotifyNearbyRequest request = NotifyNearbyRequest.builder()
                .latitude(null) // Invalid latitude
                .longitude(56.78)
                .requestId("testRequestId")
                .requestDetails("Request details")
                .radius(100.0)
                .build();

        // Act/Assert
        try {
            deviceLocationController.notifyNearbyService(request);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Latitude is required");
        }
    }

    @Test
    void testGetUserLocation_Success() {
        // Arrange
        String userId = "testUserId";
        DeviceLocation deviceLocation = DeviceLocation.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        when(deviceLocationService.getDeviceLocationByUserId(userId)).thenReturn(Optional.of(deviceLocation));

        // Act
        ResponseEntity<AbstractBaseApiResponse<DeviceLocation>> response =
                deviceLocationController.getUserLocation(userId);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(deviceLocation);

        verify(deviceLocationService, times(1)).getDeviceLocationByUserId(userId);
    }

    @Test
    void testGetUserLocation_NotFound() {
        // Arrange
        String userId = "nonExistentUserId";

        when(deviceLocationService.getDeviceLocationByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<AbstractBaseApiResponse<DeviceLocation>> response =
                deviceLocationController.getUserLocation(userId);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();

        verify(deviceLocationService, times(1)).getDeviceLocationByUserId(userId);
    }
        @Test
        void testUpdateUserStatus_Success () {
            // Arrange
            String userId = "testUserId";
            DeviceStatus status = DeviceStatus.AVAILABLE;

            when(deviceLocationService.updateUserStatus(userId, status)).thenReturn(true);

            // Act
            ResponseEntity<AbstractBaseApiResponse<String>> response =
                    deviceLocationController.updateUserStatus(userId, status);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isEqualTo("User status updated to: " + status);

            verify(deviceLocationService, times(1)).updateUserStatus(userId, status);
        }

        @Test
        void testUpdateUserStatus_NotFound () {
            // Arrange
            String userId = "nonExistentUserId";
            DeviceStatus status = DeviceStatus.BUSY;

            when(deviceLocationService.updateUserStatus(userId, status)).thenReturn(false);

            // Act
            ResponseEntity<AbstractBaseApiResponse<String>> response =
                    deviceLocationController.updateUserStatus(userId, status);

            // Assert
            assertThat(response.getStatusCode().value()).isEqualTo(404);
            assertThat(response.getBody()).isNotNull();

            verify(deviceLocationService, times(1)).updateUserStatus(userId, status);
        }

    @Test
    void testGetNearbyDevices_SuccessWithoutServiceTypes() {
        // Arrange
        double latitude = 37.7749;
        double longitude = -122.4194;
        double radius = 1000.0;
        int maxResults = 5;

        List<DeviceLocation> nearbyDevices = List.of(
                DeviceLocation.builder().build(),
                DeviceLocation.builder().build()
        );

        when(deviceLocationService.findNearbyUsers(latitude, longitude, radius, maxResults))
                .thenReturn(nearbyDevices);

        // Act
        ResponseEntity<AbstractBaseApiResponse<List<DeviceLocation>>> response =
                deviceLocationController.getNearbyDevices(latitude, longitude, radius, null, maxResults);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(2);

        verify(deviceLocationService, times(1)).findNearbyUsers(latitude, longitude, radius, maxResults);
    }

    @Test
    void testGetNearbyDevices_SuccessWithServiceTypes() {
        // Arrange
        double latitude = 37.7749;
        double longitude = -122.4194;
        double radius = 1000.0;
        int maxResults = 5;
        List<String> serviceTypes = List.of("Delivery", "Repair");

        List<DeviceLocation> nearbyDevices = List.of(
                DeviceLocation.builder().build(),
                DeviceLocation.builder().build()
        );

        when(deviceLocationService.findNearbyUsersWithServiceConsideration(latitude, longitude, serviceTypes, radius, maxResults))
                .thenReturn(nearbyDevices);

        // Act
        ResponseEntity<AbstractBaseApiResponse<List<DeviceLocation>>> response =
                deviceLocationController.getNearbyDevices(latitude, longitude, radius, serviceTypes, maxResults);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(2);

        verify(deviceLocationService, times(1))
                .findNearbyUsersWithServiceConsideration(latitude, longitude, serviceTypes, radius, maxResults);
    }

    @Test
    void testGetNearbyDevices_NoDevicesFound() {
        // Arrange
        double latitude = 37.7749;
        double longitude = -122.4194;
        double radius = 1000.0;
        int maxResults = 5;

        when(deviceLocationService.findNearbyUsers(latitude, longitude, radius, maxResults))
                .thenReturn(List.of());

        // Act
        ResponseEntity<AbstractBaseApiResponse<List<DeviceLocation>>> response =
                deviceLocationController.getNearbyDevices(latitude, longitude, radius, null, maxResults);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEmpty();

        verify(deviceLocationService, times(1)).findNearbyUsers(latitude, longitude, radius, maxResults);
    }
}