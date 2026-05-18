package org.sds.sdslocation.repository.accessinterfacerepo;

import org.sds.sdslocation.repository.TblCountrySubDivisions;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountrySubDivisionRepo extends CrudRepository<TblCountrySubDivisions, Long> {

    @Query("""
            INSERT INTO country_sub_divisions (division_code, country_sub_division, geom)
            VALUES (:divisionCode,:subDivisionName, ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326))
            RETURNING id, division_code, country_sub_division,
                              ST_AsBinary(geom) as geom,
                              status, created_at, updated_at, created_by, updated_by
            """)
    TblCountrySubDivisions nativeCreate( String divisionCode,String subDivisionName, String geoString);

    @Modifying
    @Transactional
    @Query("""
            UPDATE country_sub_divisions
            SET
                division_code = COALESCE(:divisionCode, division_code),
                country_sub_division = COALESCE(:subDivisionName, country_sub_division),
                geom = CASE
                    WHEN :geoString IS NOT NULL THEN ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326)
                    ELSE geom
                END,
                updated_at = CURRENT_TIMESTAMP,
                updated_by = :updatedBy
            WHERE id = :id AND status = 'ACTIVE'
            """)
    int updateSubDivision(
            @Param("id") Long id,
            @Param("divisionCode") String divisionCode,
            @Param("subDivisionName") String subDivisionName,
            @Param("geoString") String geoString,
            @Param("updatedBy") String updatedBy
    );


    @Query("""
                SELECT
                  id, division_code, country_sub_division,
                  ST_AsBinary(geom) as geom,
                  status, created_at, updated_at, created_by, updated_by
                FROM country_sub_divisions
                WHERE ST_Intersects(
                    geom,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)
                )
            """)
    List<TblCountrySubDivisions> getSubDivision(@Param("lon") Double lon,
                                                @Param("lat") Double lat);

    @Query("""
        SELECT id, division_code, country_sub_division,
               ST_AsBinary(geom) as geom, status, created_at, updated_at, created_by, updated_by
        FROM country_sub_divisions
        WHERE id = :id
        """)
    Optional<TblCountrySubDivisions> findBySubDivisionId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("""
            UPDATE country_sub_divisions
            SET status = 'DELETED', updated_at = CURRENT_TIMESTAMP, updated_by = :updatedBy
            WHERE id = :id AND status = 'ACTIVE'
            """)
    int softDeleteSubDivision(@Param("id") Long id, @Param("updatedBy") String updatedBy);


    @Modifying
    @Transactional
    @Query("""
            DELETE FROM country_sub_divisions
            WHERE id = :id
            """)
    int hardDeleteSubDivision(@Param("id") Long id);


    @Query("""
            SELECT COUNT(*) > 0
            FROM country_sub_divisions
            WHERE id = :id AND status = 'ACTIVE'
            """)
    boolean existsActiveSubDivision(@Param("id") Long id);

}
