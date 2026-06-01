package org.sds.sdslocation.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.sds.sdslocation.model.DeviceLocation;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.model.enums.DeviceType;
import org.sds.sdslocation.repository.DataRepository;
import org.sds.sdslocation.repository.TblDeviceLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceLocationServiceTest {

    @Mock
    private DataRepository deviceLocationRepository;

    @InjectMocks
    private DeviceLocationService deviceLocationService;

    public DeviceLocationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateDeviceLocation_shouldUpdateDeviceLocationSuccessfully() {
        DeviceLocation deviceLocation = DeviceLocation.builder()
                .deviceId("device123")
                .userId("user123")
                .lat(12.34)
                .lon(56.78)
                .notificationToken("token123")
                .accuracy(10.0)
                .status(DeviceStatus.ONLINE)
                .deviceType(DeviceType.WEB)
                .build();

        TblDeviceLocation savedDeviceLocation = new TblDeviceLocation();
        savedDeviceLocation.setDeviceId("device123");
        savedDeviceLocation.setStatus(DeviceStatus.ONLINE.name());
        savedDeviceLocation.setDeviceType(DeviceType.WEB.name());
        when(deviceLocationRepository.saveDeviceLocation(
                eq("device123"),
                eq("user123"),
                eq(12.34),
                eq(56.78),
                eq("token123"),
                any(),
                eq(10.0),
                any(),
                eq(deviceLocation.getMetadata()),
                isNull()
        )).thenReturn(savedDeviceLocation);
        //when(savedDeviceLocation.toDeviceLocation()).thenReturn(deviceLocation);

        DeviceLocation updatedDeviceLocation = deviceLocationService.updateDeviceLocation(deviceLocation);

        assertNotNull(updatedDeviceLocation);
        assertEquals("device123", updatedDeviceLocation.getDeviceId());
        verify(deviceLocationRepository, times(1)).saveDeviceLocation(
                eq("device123"),
                eq("user123"),
                eq(12.34),
                eq(56.78),
                eq("token123"),
                any(),
                eq(10.0),
                any(),
                eq(deviceLocation.getMetadata()),
                isNull()
        );
    }

    @Test
    void updateDeviceLocation_shouldThrowExceptionOnRepositoryError() {
        DeviceLocation deviceLocation = DeviceLocation.builder()
                .deviceId("device123")
                .userId("user123")
                .lat(12.34)
                .lon(56.78)
                .deviceType(DeviceType.WEB)
                .notificationToken("token123")
                .accuracy(10.0)
                .status(DeviceStatus.AVAILABLE)
                .build();

        when(deviceLocationRepository.saveDeviceLocation(
                eq("device123"),
                eq("user123"),
                eq(12.34),
                eq(56.78),
                eq("token123"),
                any(),
                eq(10.0),
                any(),
                eq(deviceLocation.getMetadata()),
                isNull()
        )).thenThrow(new RuntimeException("Simulated database error"));

        SdsLocationException exception = assertThrows(SdsLocationException.class,
                () -> deviceLocationService.updateDeviceLocation(deviceLocation));

        assertEquals("Failed to update device location", exception.getMessage());
        verify(deviceLocationRepository, times(1)).saveDeviceLocation(
                eq("device123"),
                eq("user123"),
                eq(12.34),
                eq(56.78),
                eq("token123"),
                any(),
                eq(10.0),
                any(),
                eq(deviceLocation.getMetadata()),
                isNull()
        );
    }

    @Test
    void getDeviceLocationByUserId_shouldReturnLocationIfExists() {
        String userId = "user123";

        TblDeviceLocation tblDeviceLocation = mock(TblDeviceLocation.class);
        DeviceLocation deviceLocation = DeviceLocation.builder()
                .userId(userId)
                .build();

        when(deviceLocationRepository.findDeviceLocationByUserId(userId)).thenReturn(tblDeviceLocation);
        when(tblDeviceLocation.toDeviceLocation()).thenReturn(deviceLocation);

        Optional<DeviceLocation> result = deviceLocationService.getDeviceLocationByUserId(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(deviceLocationRepository, times(1)).findDeviceLocationByUserId(userId);
    }

    @Test
    void getDeviceLocationByUserId_shouldReturnEmptyIfNotFound() {
        String userId = "user123";

        when(deviceLocationRepository.findDeviceLocationByUserId(userId)).thenReturn(null);

        Optional<DeviceLocation> result = deviceLocationService.getDeviceLocationByUserId(userId);

        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1)).findDeviceLocationByUserId(userId);
    }

    @Test
    void getDeviceLocationByUserId_shouldHandleNullInputGracefully() {
        String userId = null;

        Optional<DeviceLocation> result = deviceLocationService.getDeviceLocationByUserId(userId);

        assertTrue(result.isEmpty());

    }


    @Test
    void updateUserStatus_shouldUpdateStatusSuccessfully() {
        String userId = "user123";
        DeviceStatus newStatus = DeviceStatus.ONLINE;

        DeviceLocation deviceLocation = DeviceLocation.builder()
                .userId(userId)
                .status(DeviceStatus.AVAILABLE)
                .build();
        TblDeviceLocation tblDeviceLocation = mock(TblDeviceLocation.class);

        when(deviceLocationRepository.updateUserStatus(userId, newStatus)).thenReturn(true);
        when(deviceLocationRepository.findDeviceLocationByUserId(userId)).thenReturn(tblDeviceLocation);
        when(tblDeviceLocation.toDeviceLocation()).thenReturn(deviceLocation);

        boolean result = deviceLocationService.updateUserStatus(userId, newStatus);

        assertTrue(result);
        assertEquals(newStatus, deviceLocation.getStatus());
        verify(deviceLocationRepository, times(1)).updateUserStatus(userId, newStatus);
        verify(deviceLocationRepository, times(1)).findDeviceLocationByUserId(userId);
    }

    @Test
    void updateUserStatus_shouldFailIfRepositoryError() {
        String userId = "user123";
        DeviceStatus newStatus = DeviceStatus.ONLINE;

        when(deviceLocationRepository.updateUserStatus(userId, newStatus)).thenThrow(new RuntimeException("Database error"));

        boolean result = deviceLocationService.updateUserStatus(userId, newStatus);

        assertFalse(result);
        verify(deviceLocationRepository, times(1)).updateUserStatus(userId, newStatus);
        verify(deviceLocationRepository, never()).findDeviceLocationByUserId(userId);
    }

    @Test
    void findNearbyUsersWithServiceConsideration_shouldReturnExpectedResults() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        List<String> serviceTypes = List.of("SERVICE_A", "SERVICE_B");
        Double radiusMeters = 5000.0;
        Integer maxResults = 10;

        TblDeviceLocation tblDevice1 = mock(TblDeviceLocation.class);
        TblDeviceLocation tblDevice2 = mock(TblDeviceLocation.class);

        when(tblDevice1.getSupportedServices()).thenReturn(new String[]{"SERVICE_A", "SERVICE_C"});
        when(tblDevice1.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user1").build());
        when(tblDevice2.getSupportedServices()).thenReturn(new String[]{"SERVICE_B"});
        when(tblDevice2.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user2").build());

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000))
                .thenReturn(List.of(tblDevice1, tblDevice2));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsersWithServiceConsideration(
                latitude, longitude, serviceTypes, radiusMeters, maxResults);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> "user1".equals(user.getUserId())));
        assertTrue(result.stream().anyMatch(user -> "user2".equals(user.getUserId())));
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsersWithServiceConsideration_shouldHandleEmptyResults() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        List<String> serviceTypes = List.of("SERVICE_A", "SERVICE_B");

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, 5000.0, 20, 1000))
                .thenReturn(List.of());

        List<DeviceLocation> result = deviceLocationService.findNearbyUsersWithServiceConsideration(
                latitude, longitude, serviceTypes, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, 5000.0, 20, 1000);
    }

    @Test
    void findNearbyUsersWithServiceConsideration_shouldHandleRepositoryErrors() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        List<String> serviceTypes = List.of("SERVICE_A");

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, 5000.0, 20, 1000))
                .thenThrow(new RuntimeException("Simulated error"));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsersWithServiceConsideration(
                latitude, longitude, serviceTypes, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, 5000.0, 20, 1000);
    }

    @Test
    void findNearbyUsers_shouldReturnExpectedResults() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 5000.0;
        Integer maxResults = 10;

        TblDeviceLocation tblDevice1 = mock(TblDeviceLocation.class);
        TblDeviceLocation tblDevice2 = mock(TblDeviceLocation.class);

        when(tblDevice1.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user1").build());
        when(tblDevice2.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user2").build());

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000))
                .thenReturn(List.of(tblDevice1, tblDevice2));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> "user1".equals(user.getUserId())));
        assertTrue(result.stream().anyMatch(user -> "user2".equals(user.getUserId())));
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsers_shouldReturnExpectedResults_WithDefaultParameters() {
        Double latitude = 12.34;
        Double longitude = 56.78;

        TblDeviceLocation tblDevice1 = mock(TblDeviceLocation.class);
        when(tblDevice1.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user1").build());

        when(deviceLocationRepository.findNearbyUsersWithService(
                latitude, longitude, 5000.0, 20, 1000))
                .thenReturn(List.of(tblDevice1));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, 5000.0, 20, 1000);
    }

    @Test
    void findNearbyUsers_shouldHandleLargeRadius() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 20000.0;
        Integer maxResults = 10;

        TblDeviceLocation tblDevice = mock(TblDeviceLocation.class);
        when(tblDevice.toDeviceLocation()).thenReturn(DeviceLocation.builder().userId("user999").build());

        when(deviceLocationRepository.findNearbyUsersWithService(
                latitude, longitude, radiusMeters, maxResults, 1000))
                .thenReturn(List.of(tblDevice));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user999", result.get(0).getUserId());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsers_shouldHandleEmptyDatabaseResults() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 5000.0;
        Integer maxResults = 5;

        when(deviceLocationRepository.findNearbyUsersWithService(
                latitude, longitude, radiusMeters, maxResults, 1000))
                .thenReturn(List.of());

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsers_shouldHandleExceptionsGracefully() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 5000.0;
        Integer maxResults = 5;

        when(deviceLocationRepository.findNearbyUsersWithService(
                latitude, longitude, radiusMeters, maxResults, 1000))
                .thenThrow(new RuntimeException("Simulated repository exception"));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsers_shouldHandleEmptyResults() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 5000.0;
        Integer maxResults = 10;

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000))
                .thenReturn(List.of());

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }

    @Test
    void findNearbyUsers_shouldHandleRepositoryErrors() {
        Double latitude = 12.34;
        Double longitude = 56.78;
        Double radiusMeters = 5000.0;
        Integer maxResults = 10;

        when(deviceLocationRepository.findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000))
                .thenThrow(new RuntimeException("Simulated error"));

        List<DeviceLocation> result = deviceLocationService.findNearbyUsers(latitude, longitude, radiusMeters, maxResults);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deviceLocationRepository, times(1))
                .findNearbyUsersWithService(latitude, longitude, radiusMeters, maxResults, 1000);
    }
}