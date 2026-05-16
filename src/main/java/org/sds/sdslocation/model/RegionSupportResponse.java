package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 1:20 PM
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RegionSupportResponse {
    private String locationArea;
    private boolean supported;
    private boolean available;
}
