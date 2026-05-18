package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author samwel.wafula
 * Created on 5/18/2026
 * Time 10:57 AM
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLocation {
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("lat")
    private double lat;
    @JsonProperty("lon")
    private double lon;
}
