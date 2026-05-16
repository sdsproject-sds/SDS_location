package org.sds.sdslocation.model.request;

import com.neovisionaries.i18n.CountryCode;
import lombok.*;
import org.sds.sdslocation.model.PolygonGeometry;

/**
 * @author samwel.wafula
 * Created on 5/16/2026
 * Update request for Country Division - only non-null fields will be updated
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryDivisionUpdateRequest {
    private PolygonGeometry polygonGeometry;
    private CountryCode countryCode;
    private String division;
    private Boolean supported;
}