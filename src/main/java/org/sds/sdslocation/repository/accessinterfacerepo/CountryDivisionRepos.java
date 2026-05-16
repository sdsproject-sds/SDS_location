package org.sds.sdslocation.repository.accessinterfacerepo;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.sds.sdslocation.repository.TblCountryDivisions;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:35 PM
 */
@Repository
public interface CountryDivisionRepos extends CrudRepository<TblCountryDivisions, String> {

    @Query("""
            INSERT INTO tbl_country_divisions (country_iso2,division_code, division, geom)
            VALUES (:countryIso2, :divisionCode,:divisionName, ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326))
            RETURNING id, country_iso2, division, division_code,
                                  ST_AsBinary(geom) as geom, supported,
                                  status, created_at, updated_at, created_by, updated_by
            """)
    TblCountryDivisions nativeCreate(String countryIso2, String divisionCode, String divisionName, String geoString);

    @Modifying
    @Transactional
    @Query("""
            UPDATE tbl_country_divisions
            SET 
                country_iso2 = COALESCE(:countryIso2, country_iso2),
                division = COALESCE(:divisionName, division),
                geom = CASE 
                    WHEN :geoString IS NOT NULL THEN ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326)
                    ELSE geom
                END,
                supported = COALESCE(:supported, supported),
                updated_at = CURRENT_TIMESTAMP,
                updated_by = :updatedBy
            WHERE division_code = :divisionCode AND status = 'ACTIVE'
            """)
    int updateDivision(
            @Param("divisionCode") String divisionCode,
            @Param("countryIso2") String countryIso2,
            @Param("divisionName") String divisionName,
            @Param("geoString") String geoString,
            @Param("supported") Boolean supported,
            @Param("updatedBy") String updatedBy
    );


    @Query("""
                SELECT *
                FROM tbl_country_divisions
                WHERE ST_Intersects(
                    geom,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)
                )
            """)
    List<TblCountryDivisions> getDivision(@Param("lon") Double lon,
                                                @Param("lat") Double lat);


    @Query("""
            SELECT id, country_iso2, division, division_code,
                   ST_AsBinary(geom) as geom, supported,
                   status, created_at, updated_at, created_by, updated_by
            FROM tbl_country_divisions
            WHERE division_code = :divisionCode
            """)

    Optional<TblCountryDivisions> findByDivisionCode(@Param("divisionCode") String divisionCode);


    @Query("""
            SELECT id, country_iso2, division, division_code,
                   ST_AsBinary(geom) as geom, supported,
                   status, created_at, updated_at, created_by, updated_by
            FROM tbl_country_divisions
            WHERE division_code = :division_code
            """)
    Optional<TblCountryDivisions> findByDivisionId(@NonNull @Param("division_code") String division_code);

    @Modifying
    @Transactional
    @Query("""
            UPDATE tbl_country_divisions
            SET status = 'DELETED', updated_at = CURRENT_TIMESTAMP, updated_by = :updatedBy
            WHERE division_code = :divisionCode AND status = 'ACTIVE'
            """)
    int softDeleteDivision(@Param("divisionCode") String divisionCode, @Param("updatedBy") String updatedBy);


    @Modifying
    @Transactional
    @Query("""
            DELETE FROM tbl_country_divisions
            WHERE division_code = :divisionCode
            """)
    int hardDeleteDivision(@Param("divisionCode") String divisionCode);

    @Query("""
            SELECT COUNT(*) > 0
            FROM tbl_country_divisions
            WHERE division_code = :divisionCode AND status = 'ACTIVE'
            """)
    boolean existsActiveDivision(@Param("divisionCode") String divisionCode);

    @Query("""
            SELECT COUNT(*)
            FROM country_sub_divisions
            WHERE division_code = :divisionCode AND status = 'ACTIVE'
            """)
    long countActiveSubDivisions(@Param("divisionCode") String divisionCode);


}
