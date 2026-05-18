package org.sds.sdslocation.exeption;

/**
 * @author Joseph.Kibe. Created On 03 May 2026 20:09
 */
public class SdsLocationNotFoundException extends SdsLocationException {
    public SdsLocationNotFoundException(String message) {
        super(message);
    }

    public SdsLocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SdsLocationNotFoundException(Throwable cause) {
        super(cause);
    }
}
