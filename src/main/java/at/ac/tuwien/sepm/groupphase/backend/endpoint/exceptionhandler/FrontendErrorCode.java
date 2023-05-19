package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

/**
 * Holds all error codes that can be sent to the frontend.
 * This is used to show correctly translated error messages in the frontend
 * without tightly coupling the translation keys to the backend.
 * To reduce complexity, the codes are hardcoded in the frontend and may not be changed after initial
 * inclusion.
 */
public enum FrontendErrorCode {

    UNKNOWN(0),

    // Login
    LOGIN_BAD_CREDENTIALS(1),
    LOGIN_VALIDATION_USERNAME_OR_EMAIL_EXISTS(2),

    // Registration
    REGISTER_VALIDATION_USERNAME_OR_EMAIL_EXISTS(10),
    REGISTER_VERIFYEMAIL_FAILED(11),
    REGISTER_GENERIC_FAILED(12),

    // Logic
    LOGIC_PARSING_ERROR(14),

    // User update
    USER_UPDATE_VALIDATION_USERNAME_OR_EMAIL_EXISTS(20),
    USER_UPDATE_GENERIC_FAILED(21);

    public final int code;

    FrontendErrorCode(int code) {
        this.code = code;
    }

}
