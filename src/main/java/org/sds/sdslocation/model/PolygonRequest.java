package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:14 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PolygonRequest {
    private Map<String, String> properties;
    @JsonProperty("geometry")
    private PolygonGeometry polygonGeometry;

    //extra for division
    @JsonProperty("iso2")
    private String countryIsoCode2;
    private String division;
    @JsonProperty("division_code")
    private String divisionCode;
}
