package org.sds.sdslocation.model.request;


import com.neovisionaries.i18n.CountryCode;
import lombok.*;
import org.sds.sdslocation.model.PolygonGeometry;

/**
 * @author Joseph.Kibe. Created On 15 May 2026 23:05
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryDivisionRequest {
    private PolygonGeometry polygonGeometry;
    private CountryCode countryCode;
    private String division;
}
