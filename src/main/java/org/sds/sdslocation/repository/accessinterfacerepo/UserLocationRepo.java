package org.sds.sdslocation.repository.accessinterfacerepo;

import org.sds.sdslocation.repository.TblDeviceLocation;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocationRepo extends CrudRepository<TblDeviceLocation, String> {

    //@Modifying
    @Query(value = """
            INSERT INTO user_locations (
                device_id,
                user_id,
                lat,
                lon,
                location,
                created_at,
                updated_at
            )
            VALUES (
                :deviceId,
                :userId,
                :lat,
                :lon,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                NOW(),
                NOW()
            )
            ON CONFLICT (device_id,user_id)
            DO UPDATE SET
                lat = EXCLUDED.lat,
                lon = EXCLUDED.lon,
                location = EXCLUDED.location,
                updated_at = NOW()
            RETURNING device_id,user_id,lat,lon
            """)
    TblDeviceLocation saveUserLocation(
            @Param("deviceId") String deviceId,
            @Param("userId") String userId,
            @Param("lat") Double lat,
            @Param("lon") Double lon
    );


    @Query(value = """
            SELECT device_id,user_id 
            FROM user_locations
            WHERE ST_DWithin(
                location,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radius
            )
            AND updated_at >= NOW() - INTERVAL '20 minutes'
            """)
    List<TblDeviceLocation> findNearbyUsers(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radius") Integer radius
    );

}
