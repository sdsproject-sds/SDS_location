package org.sds.sdslocation.repository;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import lombok.Setter;
import org.sds.sdslocation.model.CountryDivision;
import org.springframework.data.annotation.Id;
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
public class TblCountryDivisions extends AuditableEntity {
    @Column("id")
    private long id;
    @Column("country_iso2")
    private String iso2CountryCode;
    private String division;
    @Id
    private String divisionCode;
    @Column("geom")
    private org.locationtech.jts.geom.Geometry geom;
    private boolean supported;


    public CountryDivision toCountryDivision(){
        return CountryDivision.builder()
                .countryCode(CountryCode.valueOf(iso2CountryCode))
                .division(division)
                .divisionId(divisionCode)
                .geometry(geom)
                .build();
    }
}
