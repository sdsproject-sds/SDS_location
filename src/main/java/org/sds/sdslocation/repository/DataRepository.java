package org.sds.sdslocation.repository;

import java.util.Map;
import org.sds.sdslocation.model.CountryDivision;
import org.sds.sdslocation.model.DeviceLocation;
import org.sds.sdslocation.model.SubDivision;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.repository.accessinterfacerepo.CountryDivisionRepos;
import org.sds.sdslocation.repository.accessinterfacerepo.CountrySubDivisionRepo;
import org.sds.sdslocation.repository.accessinterfacerepo.DeviceLocationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:53 PM
 */
@Service
public class DataRepository {

    private final CountryDivisionRepos countryDivision;
    private final CountrySubDivisionRepo countrySubDivisionRepo;
    private final DeviceLocationRepo deviceLocationRepo;

    public DataRepository(CountryDivisionRepos countryDivision, CountrySubDivisionRepo countrySubDivisionRepo, DeviceLocationRepo deviceLocationRepo) {
        this.countryDivision = countryDivision;
        this.countrySubDivisionRepo = countrySubDivisionRepo;
        this.deviceLocationRepo = deviceLocationRepo;
    }

    public DeviceLocation saveUserLocation(DeviceLocation location) {
        return deviceLocationRepo.upsertDeviceLocation(
                        location.getDeviceId(),
                        location.getUserId(),
                        location.getLat(),
                        location.getLon(),
                        location.getNotificationToken(),
                        location.getDeviceType().name(),
                        location.getAccuracy(),
                        location.getStatus().name(),
                        location.getMetadata(),
                        location.getSupportedServices().toArray(String[]::new)
                )
                .toDeviceLocation();
    }

    public List<TblDeviceLocation> findNearbyUsers(double lat, double lon) {
        return deviceLocationRepo.findNearbyUsers(lat, lon, 2000.0);
    }

    public SubDivision saveSubDivision(String geoJson, String divisionCode, String subDivisionName) {
        return countrySubDivisionRepo.nativeCreate(divisionCode, subDivisionName, geoJson)
                .toSubDivision();
    }

    public CountryDivision saveDivision(String countryIso2, String divisionCode, String divisionName, String geoString) {
        return countryDivision.nativeCreate(countryIso2, divisionCode, divisionName, geoString)
                .toCountryDivision();
    }

    public boolean updateDivision(String divisionCode, String countryIso2, String divisionName,
                                  String geoString, Boolean supported, String updatedBy) {
        int rowsAffected = countryDivision.updateDivision(
                divisionCode,
                countryIso2,
                divisionName,
                geoString,
                supported,
                updatedBy
        );
        return rowsAffected > 0;
    }

    public boolean updateSubDivision(Long id, String divisionCode, String subDivisionName,
                                     String geoString, String updatedBy) {
        int rowsAffected = countrySubDivisionRepo.updateSubDivision(
                id,
                divisionCode,
                subDivisionName,
                geoString,
                updatedBy
        );
        return rowsAffected > 0;
    }

    public List<TblCountrySubDivisions> getSubDivision(Double lon, Double lat) {
        return countrySubDivisionRepo.getSubDivision(lon, lat);
    }

    public List<TblCountryDivisions> getDivision(Double lon, Double lat) {
        return countryDivision.getDivision(lon, lat);
    }

    public TblCountryDivisions getDivisionByDivisionCode(String divisionCode) {
        return countryDivision.findByDivisionCode(divisionCode)
                .orElse(null);
    }

    public TblCountryDivisions getDivisionById(String id) {
        return countryDivision.findByDivisionId(id)
                .orElse(null);
    }

    public SubDivision getSubDivisionById(Long id) {
        return countrySubDivisionRepo.findBySubDivisionId(id)
                .map(TblCountrySubDivisions::toSubDivision)
                .orElse(null);
    }

    public boolean deleteSubDivision(Long id, String deletedBy) {
        int rowsAffected = countrySubDivisionRepo.softDeleteSubDivision(id, deletedBy);
        return rowsAffected > 0;
    }

    public boolean hardDeleteSubDivision(Long id) {
        int rowsAffected = countrySubDivisionRepo.hardDeleteSubDivision(id);
        return rowsAffected > 0;
    }

    public boolean subDivisionExists(Long id) {
        return countrySubDivisionRepo.existsActiveSubDivision(id);
    }

    public boolean deleteDivision(String divisionCode, String deletedBy) {
        int rowsAffected = countryDivision.softDeleteDivision(divisionCode, deletedBy);
        return rowsAffected > 0;
    }

    public boolean hardDeleteDivision(String divisionCode) {
        int rowsAffected = countryDivision.hardDeleteDivision(divisionCode);
        return rowsAffected > 0;
    }

    public boolean divisionExists(String divisionCode) {
        return countryDivision.existsActiveDivision(divisionCode);
    }

    public long countActiveSubDivisions(String divisionCode) {
        return countryDivision.countActiveSubDivisions(divisionCode);
    }

    public boolean isCountryDivisionAvailable(String divisionCode) {
        return countryDivision.findByDivisionCode(divisionCode).isPresent();
    }

    /**
     * Save or update device location (UPSERT)
     */
    public TblDeviceLocation saveDeviceLocation(String deviceId, String userId, Double lat, Double lon,
                                                String notificationToken, String deviceType, Double accuracy,
                                                String status, Map<String, Object> metadata, String[] supportedServices) {
        return deviceLocationRepo.upsertDeviceLocation(
                deviceId, userId, lat, lon, notificationToken,
                deviceType, accuracy, status, metadata, supportedServices
        );
    }

    /**
     * Find device location by userId
     */
    public TblDeviceLocation findDeviceLocationByUserId(String userId) {
        return deviceLocationRepo.findByUserId(userId).orElse(null);
    }

    /**
     * Find device location by deviceId
     */
    public TblDeviceLocation findDeviceLocationByDeviceId(String deviceId) {
        return deviceLocationRepo.findById(deviceId).orElse(null);
    }

    /**
     * Find nearby users with service filtering (PostgresSQL fallback)
     */
    public List<TblDeviceLocation> findNearbyUsersWithService(Double latitude, Double longitude,
                                                              Double radiusMeters,
                                                              Integer maxResults, int timeoutMinutes) {
        return deviceLocationRepo.findNearbyAvailableUsers(
                latitude, longitude, radiusMeters,
                timeoutMinutes, maxResults
        );
    }

    /**
     * Find users supporting a specific service
     */
    public List<TblDeviceLocation> findUsersByServiceType(String serviceType) {
        return deviceLocationRepo.findByServiceType(serviceType);
    }

    /**
     * Update user status
     */
    public boolean updateUserStatus(String userId, DeviceStatus status) {
        int rowsAffected = deviceLocationRepo.updateUserStatus(userId, status.name());
        return rowsAffected > 0;
    }

    /**
     * Update device location coordinates only
     */
    public boolean updateDeviceCoordinates(String deviceId, Double lat, Double lon) {
        int rowsAffected = deviceLocationRepo.updateCoordinates(deviceId, lat, lon);
        return rowsAffected > 0;
    }

    /**
     * Check if the device location exists
     */
    public boolean deviceLocationExists(String deviceId) {
        return deviceLocationRepo.existsById(deviceId);
    }

    /**
     * Check if a user has an active location
     */
    public boolean userHasActiveLocation(String userId) {
        return deviceLocationRepo.existsActiveLocationByUserId(userId);
    }

    /**
     * Clean up stale locations
     */
    public int deleteStaleDeviceLocations(int timeoutMinutes) {
        return deviceLocationRepo.deleteStaleLocations(timeoutMinutes);
    }

    /**
     * Count active users
     */
    public long countActiveUsers(String status, int timeoutMinutes) {
        return deviceLocationRepo.countActiveUsers(status, timeoutMinutes);
    }

    /**
     * Delete device location
     */
    public boolean deleteDeviceLocation(String deviceId) {
        int rowsAffected = deviceLocationRepo.deleteByDeviceId(deviceId);
        return rowsAffected > 0;
    }

    /**
     * Get all device locations for a user (if multiple devices)
     */
    public List<TblDeviceLocation> findAllDeviceLocationsByUserId(String userId) {
        return deviceLocationRepo.findAllByUserId(userId);
    }

    /**
     * Update notification token
     */
    public boolean updateNotificationToken(String deviceId, String notificationToken) {
        int rowsAffected = deviceLocationRepo.updateNotificationToken(deviceId, notificationToken);
        return rowsAffected > 0;
    }
}
