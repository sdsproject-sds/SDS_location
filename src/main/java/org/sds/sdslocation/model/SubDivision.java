package org.sds.sdslocation.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SubDivision {
    @JsonIgnore
    private Geometry geometry;
    private String subDivisionName;
    private String divisionId;
    private String subDivisionId;

    @JsonProperty("geometry")
    public PolygonGeometry getPolygonGeometry() {
        return PolygonGeometry.fromGeometry(this.geometry);
    }


}
