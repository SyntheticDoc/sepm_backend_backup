package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that a user-triggered process has failed and
 * the issue needs to be properly communicated with the user.
 */
public class ProcessException extends AbstractFrontendErrorException {

    public ProcessException(String message, FrontendErrorCode errorCode) {
        super(message, errorCode);
    }

    public ProcessException(String message, FrontendErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
