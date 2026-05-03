package org.sds.sdslocation.repository.accessinterfacerepo;

import org.sds.sdslocation.repository.TblCountryDivisions;
import org.sds.sdslocation.repository.TblCountrySubDivisions;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:35 PM
 */
@Repository
public interface CountryDivision extends CrudRepository<TblCountryDivisions, String> {

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO tbl_country_divisions (country_iso2,division_code, division, geom)
            VALUES (:countryIso2, :divisionCode,:divisionName, ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326))
            """)
    void nativeCreate(String countryIso2, String divisionCode, String divisionName, String geoString);

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
}
