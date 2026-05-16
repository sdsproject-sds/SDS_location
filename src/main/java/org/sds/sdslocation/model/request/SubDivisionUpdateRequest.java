package org.sds.sdslocation.model.request;

import lombok.*;
import org.sds.sdslocation.model.PolygonGeometry;

/**
 * @author samwel.wafula
 * Created on 5/16/2026
 * Update request for Sub Division - only non-null fields will be updated
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubDivisionUpdateRequest {
    private PolygonGeometry polygonGeometry;
    private String divisionId;
    private String subDivisionName;
}