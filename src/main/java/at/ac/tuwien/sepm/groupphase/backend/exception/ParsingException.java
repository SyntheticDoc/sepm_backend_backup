package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;

/**
 * Signals that an error occurred while parsing.
 */
public class ParsingException extends AbstractFrontendErrorException {
    public ParsingException(String message, FrontendErrorCode errorCode) {
        super(message, errorCode);
    }
}
