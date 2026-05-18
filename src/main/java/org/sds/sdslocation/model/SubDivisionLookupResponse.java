package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author samwel.wafula
 * Created on 5/3/2026
 * Time 1:20 PM
 */
@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubDivisionLookupResponse extends AbstractLocationCoverage{
    private SubDivision subDivision;
}
