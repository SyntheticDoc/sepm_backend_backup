package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that there was an UNEXPECTED problem in the
 * persistence layer which inevitably makes the request
 * fail.
 */
public class PersistenceException extends AbstractFrontendErrorException {

    /**
     * Construct exception with message and error code.
     *
     * @see PersistenceException
     * @param message exception message
     */
    public PersistenceException(String message, FrontendErrorCode errorCode) {
        super(message, errorCode);
    }

    /**
     * Construct exception with message, error code and cause.
     *
     * @see PersistenceException
     * @param message exception message
     * @param cause exception cause
     */
    public PersistenceException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

}
