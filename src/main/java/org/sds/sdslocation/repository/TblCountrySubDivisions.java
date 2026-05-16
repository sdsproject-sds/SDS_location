package org.sds.sdslocation.repository;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.sds.sdslocation.model.SubDivision;
import org.sds.sdslocation.model.request.SubDivisionRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.nio.file.LinkOption;
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
    @Id
    @Column("id")
    private Long id;
    @Column("division_code")
    private String divisionCode;
    @Column("country_sub_division")
    private String countrySubDivisionName;
    @Column("geom")
    private org.locationtech.jts.geom.Geometry geom;


    public SubDivision toSubDivision(){
        return SubDivision.builder()
                .geometry(geom)
                .subDivisionName(countrySubDivisionName)
                .divisionId(divisionCode)
                .subDivisionId(String.valueOf(id))
                .build();
    }
}
