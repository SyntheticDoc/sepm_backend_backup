package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public interface UserRegistrationService {

    /**
     * Register (persist) a single new user with given data.
     * The username and e-mail are stored lower-case.
     *
     * @param locale users' local
     * @param registerDto registration data
     * @return new authentication token
     */
    String registerUser(Locale locale, UserAccountDataDto registerDto);

    /**
     * Send verification e-mai for the given user in the given language.
     *
     * @param locale locale of the request
     * @param loginKey generated login key for registration
     * @param user current application user
     * @throws UnsupportedEncodingException is thrown if the character encoding used is not supported
     * @throws MessagingException basic messaging exception, is thrown if authentication failed, message parsing failed,
     *     the sending of the mail failed and so on
     */
    void sendVerificationEmail(Locale locale, String loginKey, ApplicationUser user)
        throws MessagingException, UnsupportedEncodingException;

    /**
     * Generate a one-time login key.
     *
     * @return new newly generated key
     */
    String generateLoginKey();

}
