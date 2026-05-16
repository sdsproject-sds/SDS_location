package org.sds.sdslocation.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

/**
 * @author Joseph.Kibe. Created On 15 May 2026 23:05
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryDivision {
    @JsonIgnore
    private Geometry geometry;
    private CountryCode countryCode;
    private String division;
    private String divisionId;

    @JsonProperty("geometry")
    public PolygonGeometry getPolygonGeometry() {
        return PolygonGeometry.fromGeometry(this.geometry);
    }

}
