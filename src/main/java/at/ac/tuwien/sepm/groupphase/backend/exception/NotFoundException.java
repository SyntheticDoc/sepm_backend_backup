package at.ac.tuwien.sepm.groupphase.backend.exception;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

public class NotFoundException extends AbstractFrontendErrorException {

    /**
     * Deprecated.
     *
     * @param message message to display
     * @deprecated use one of the constructors with frontend error code
     */
    public NotFoundException(String message) {
        super(message, null);
    }

    public NotFoundException(String message, FrontendErrorCode errorCode) {
        super(message, errorCode);
    }

    public NotFoundException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public NotFoundException(Throwable cause, FrontendErrorCode errorCode) {
        super(cause, errorCode);
    }
}
