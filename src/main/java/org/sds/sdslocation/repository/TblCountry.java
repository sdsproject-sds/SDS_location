package org.sds.sdslocation.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * @author samwel.wafula
 * Created on 4/14/2026
 * Time 4:16 PM
 */
@Getter
@Setter
@Table("tbl_country")
public class TblCountry {
    private String id;
    @Column("name")
    private String countryName;
    @Column("iso2")
    private String iso2CountryCode;
    @Column("iso3")
    private String iso3CountryCode;
    private Timestamp createdAt;
    private String updatedAt;
}
