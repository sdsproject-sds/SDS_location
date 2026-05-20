package org.sds.sdslocation.repository.accessinterfacerepo;

import java.util.Map;
import java.util.Optional;
import org.sds.sdslocation.repository.TblDeviceLocation;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface DeviceLocationRepo extends CrudRepository<TblDeviceLocation, String> {

    /**
     * UPSERT device location with RETURNING clause
     */
    @Query("""
        INSERT INTO device_locations (device_id, user_id, lat, lon, location,
                                    notification_token, device_type, accuracy, status,
                                    metadata, supported_services, created_at, updated_at)
        VALUES (:deviceId, :userId, :lat, :lon,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326),
                :notificationToken, :deviceType, :accuracy, :status,
                CAST(:metadata AS jsonb), :supportedServices, NOW(), NOW())
        ON CONFLICT (device_id)
        DO UPDATE SET
            user_id = EXCLUDED.user_id,
            lat = EXCLUDED.lat,
            lon = EXCLUDED.lon,
            location = EXCLUDED.location,
            notification_token = EXCLUDED.notification_token,
            device_type = EXCLUDED.device_type,
            accuracy = EXCLUDED.accuracy,
            status = EXCLUDED.status,
            metadata = EXCLUDED.metadata,
            supported_services = EXCLUDED.supported_services,
            updated_at = NOW()
        RETURNING device_id, user_id, lat, lon, ST_AsBinary(location) as location,
                  notification_token, device_type, accuracy, status, metadata,
                  supported_services, created_at, updated_at
        """)
    TblDeviceLocation upsertDeviceLocation(
            @Param("deviceId") String deviceId,
            @Param("userId") String userId,
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("notificationToken") String notificationToken,
            @Param("deviceType") String deviceType,
            @Param("accuracy") Double accuracy,
            @Param("status") String status,
            @Param("metadata") Map<String, Object> metadata,
            @Param("supportedServices") String[] supportedServices
    );

    /**
     * fallback query with distance calculation
     */
    @Query("""
        SELECT device_id, user_id, lat, lon, ST_AsBinary(location) as location,
               notification_token, device_type, accuracy, status, metadata,
               supported_services, created_at, updated_at,
               ST_Distance(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) as distance_meters
        FROM device_locations
        WHERE status IN ('AVAILABLE', 'ONLINE')
        AND updated_at >= NOW() - (:timeoutMinutes * INTERVAL '1 minute')
        AND ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326) , :radiusMeters)
        ORDER BY distance_meters
        LIMIT :maxResults
        """)
    List<TblDeviceLocation> findNearbyAvailableUsers(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters,
            @Param("timeoutMinutes") Integer timeoutMinutes,
            @Param("maxResults") Integer maxResults
    );

    /**
     * Find by userId
     */
    @Query("""
        SELECT device_id, user_id, lat, lon, ST_AsBinary(location) as location,
               notification_token, device_type, accuracy, status, metadata,
               supported_services, created_at, updated_at
        FROM device_locations
        WHERE user_id = :userId
        LIMIT 1
        """)
    Optional<TblDeviceLocation> findByUserId(@Param("userId") String userId);

    /**
     * Find all devices for a user
     */
    @Query("""
        SELECT device_id, user_id, lat, lon, ST_AsBinary(location) as location,
               notification_token, device_type, accuracy, status, metadata,
               supported_services, created_at, updated_at
        FROM device_locations
        WHERE user_id = :userId
        """)
    List<TblDeviceLocation> findAllByUserId(@Param("userId") String userId);

    /**
     * Find by service type
     */
    @Query("""
        SELECT device_id, user_id, lat, lon, ST_AsBinary(location) as location,
               notification_token, device_type, accuracy, status, metadata,
               supported_services, created_at, updated_at
        FROM device_locations
        WHERE :serviceType = ANY(supported_services)
        AND status IN ('AVAILABLE', 'ONLINE')
        AND updated_at > NOW() - INTERVAL '10 minutes'
        """)
    List<TblDeviceLocation> findByServiceType(@Param("serviceType") String serviceType);

    /**
     * Update user status
     */
    @Modifying
    @Transactional
    @Query("UPDATE device_locations SET status = :status, updated_at = NOW() WHERE user_id = :userId")
    int updateUserStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * Update coordinates only
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE device_locations
        SET lat = :lat, lon = :lon,
            location = ST_SetSRID(ST_MakePoint(:lon, :lat), 4326),
            updated_at = NOW()
        WHERE device_id = :deviceId
        """)
    int updateCoordinates(@Param("deviceId") String deviceId,
                          @Param("lat") Double lat,
                          @Param("lon") Double lon);

    /**
     * Update notification token
     */
    @Modifying
    @Transactional
    @Query("UPDATE device_locations SET notification_token = :token, updated_at = NOW() WHERE device_id = :deviceId")
    int updateNotificationToken(@Param("deviceId") String deviceId, @Param("token") String token);

    /**
     * Check if a user has an active location
     */
    @Query("""
        SELECT COUNT(*) > 0
        FROM device_locations
        WHERE user_id = :userId
        AND status IN ('AVAILABLE', 'ONLINE')
        AND updated_at > NOW() - INTERVAL '10 minutes'
        """)
    boolean existsActiveLocationByUserId(@Param("userId") String userId);

    /**
     * Count active users
     */
    @Query("""
        SELECT COUNT(*)
        FROM device_locations
        WHERE status = :status
        AND updated_at > NOW() - INTERVAL ':timeoutMinutes minutes'
        """)
    Long countActiveUsers(@Param("status") String status, @Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * Delete stale locations
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM device_locations WHERE updated_at < NOW() - INTERVAL ':timeoutMinutes minutes'")
    int deleteStaleLocations(@Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * Delete by device ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM device_locations WHERE device_id = :deviceId")
    int deleteByDeviceId(@Param("deviceId") String deviceId);

    /**
     * Existing method for nearby users (keeping for compatibility)
     */
    @Query("""
        SELECT device_id, user_id, lat, lon, ST_AsBinary(location) as location,
               notification_token, device_type, accuracy, status, metadata,
               supported_services, created_at, updated_at
        FROM device_locations
        WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), :radiusMeters)
        AND status IN ('AVAILABLE', 'ONLINE')
        """)
    List<TblDeviceLocation> findNearbyUsers(@Param("lat") Double lat,
                                            @Param("lon") Double lon,
                                            @Param("radiusMeters") Double radiusMeters);

}

