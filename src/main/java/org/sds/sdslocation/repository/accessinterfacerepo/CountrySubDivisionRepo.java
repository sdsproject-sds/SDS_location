package org.sds.sdslocation.repository.accessinterfacerepo;

import org.sds.sdslocation.repository.TblCountrySubDivisions;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CountrySubDivisionRepo extends CrudRepository<TblCountrySubDivisions, String> {
    @Modifying
    @Transactional
    @Query("""
            INSERT INTO country_sub_divisions (division_code, country_sub_division, geom)
            VALUES (:divisionCode,:subDivisionName, ST_SetSRID(ST_GeomFromGeoJSON(:geoString), 4326))
            """)
    void nativeCreate( String divisionCode,String subDivisionName, String geoString);

    @Query("""
                SELECT *
                FROM country_sub_divisions
                WHERE ST_Intersects(
                    geom,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)
                )
            """)
    List<TblCountrySubDivisions> getSubDivision(@Param("lon") Double lon,
                                                @Param("lat") Double lat);
}
