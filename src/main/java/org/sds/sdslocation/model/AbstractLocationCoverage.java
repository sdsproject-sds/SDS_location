package org.sds.sdslocation.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Joseph.Kibe. Created On 16 May 2026 20:08
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractLocationCoverage {
    public boolean supported;
    public boolean available;
}
