package org.sds.sdslocation.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 2:15 PM
 */
@Getter
@Setter
@Table("tbl_country_divisions")
public class TblCountryDivisions {
    private String id;
    @Column("country_iso2")
    private String iso2CountryCode;
    private String division;
    private String divisionCode;
    @Column("geom")
    private org.locationtech.jts.geom.Geometry geom;
    private boolean supported;
    private String createdAt;
    private String updatedAt;


}
