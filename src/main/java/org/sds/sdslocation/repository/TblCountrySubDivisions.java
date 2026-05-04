package org.sds.sdslocation.repository;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * @author samwel.wafula
 * Created on 4/14/2026
 * Time 4:19 PM
 */
@Getter
@Setter
@Table("country_sub_divisions")
public class TblCountrySubDivisions extends AuditableEntity {
    private String id;
    @Column("division_code")
    private String divisionCode;
    @Column("country_sub_division")
    private String countrySubDivisionName;
    @Column("geom")
    private org.locationtech.jts.geom.Geometry geom;
}
