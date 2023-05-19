package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that a user-given value
 * has caused a collision when creating or modifying a resource.
 */
public class AbstractFrontendErrorException extends RuntimeException {

    /**
     * Error code used to translate in frontend.
     */
    private final FrontendErrorCode errorCode;

    public AbstractFrontendErrorException(String message, FrontendErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AbstractFrontendErrorException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AbstractFrontendErrorException(Throwable cause, FrontendErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public FrontendErrorCode getErrorCode() {
        return errorCode;
    }

}
