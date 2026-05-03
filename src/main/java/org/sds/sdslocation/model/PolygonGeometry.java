package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:15 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PolygonGeometry {
    private String type;

    private List<List<List<Double>>> coordinates;
}
