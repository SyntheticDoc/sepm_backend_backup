package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that a user-given value
 * has caused a collision when creating or modifying a resource.
 */
public class CollisionException extends ValidationException {
    private static final long serialVersionUID = -4702681174737888941L;

    /**
     * Create exception with frontend error code.
     *
     * @see CollisionException
     * @param message exception message
     * @param errorCode frontend error code
     * @param cause exception cause
     */
    public CollisionException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
