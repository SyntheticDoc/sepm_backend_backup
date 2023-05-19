package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that user-given input was determined to be erroneous
 * and is not accepted.
 */
public class ValidationException extends AbstractFrontendErrorException {

    /**
     * Constructs an exception with message and error code.
     *
     * @see ValidationException
     * @param message exception message
     * @param errorCode frontend error code
     */
    public ValidationException(String message, FrontendErrorCode errorCode) {
        this(message, errorCode, null);
    }

    /**
     * This constructor is to be used by child-exceptions only.
     * Plain validation exceptions are not caused by other exceptions,
     * but are to be thrown manually.
     *
     * @see ValidationException
     * @param message exception message
     * @param errorCode frontend error code
     * @param cause exception cause
     */
    protected ValidationException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

}
