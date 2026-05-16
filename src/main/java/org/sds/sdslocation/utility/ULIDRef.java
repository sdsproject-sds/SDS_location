package org.sds.sdslocation.utility;


import de.huxhorn.sulky.ulid.ULID;

/**
 * @author Joseph.Kibe. Created On 15 May 2026 23:23
 */

public class ULIDRef {

    public static String get() {
        ULID ulid = new ULID();
        return ulid.nextULID().toUpperCase();
    }
}
