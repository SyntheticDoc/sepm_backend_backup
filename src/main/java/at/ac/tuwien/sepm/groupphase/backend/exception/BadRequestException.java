package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * For requests where to user sent invalid data.
 */
public class BadRequestException extends AbstractFrontendErrorException {

    public BadRequestException(String message, FrontendErrorCode errorCode) {
        super(message, errorCode, null);
    }

    public BadRequestException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

}
