package org.sds.sdslocation.exeption;

/**
 * @author Joseph.Kibe. Created On 03 May 2026 20:09
 */
public class SdsLocationException extends RuntimeException {
    public SdsLocationException(String message) {
        super(message);
    }

    public SdsLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SdsLocationException(Throwable cause) {
        super(cause);
    }
}
