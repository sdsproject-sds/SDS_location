package org.sds.sdslocation.controller;


import com.sds.integration.commons.model.AbstractBaseApiResponse;
import lombok.*;

/**
 * @author Joseph.Kibe. Created On 03 May 2026 20:14
 */

@Setter
@Getter
@Builder
public class ApiResponse<T> extends AbstractBaseApiResponse<T> {
    public ApiResponse() {
        super();
    }
}
