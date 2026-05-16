package org.sds.sdslocation.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.sds.sdslocation.model.PolygonGeometry;

/**
 * @author Joseph.Kibe. Created On 15 May 2026 23:05
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubDivisionRequest {
    private PolygonGeometry polygonGeometry;
    private String subDivisionName;
    private String divisionId;

}
