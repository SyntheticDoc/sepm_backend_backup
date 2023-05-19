package at.ac.tuwien.sepm.groupphase.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Holds configuration props for user registration.
 */
@ConfigurationProperties(prefix = "application.registration")
@Component
public class RegistrationProperties {

    private String emailSenderAddress;
    private String emailSenderName;
    private String emailVerifyUri;

    public String getEmailSenderAddress() {
        return emailSenderAddress;
    }

    public void setEmailSenderAddress(String emailSenderAddress) {
        this.emailSenderAddress = emailSenderAddress;
    }

    public String getEmailSenderName() {
        return emailSenderName;
    }

    public void setEmailSenderName(String emailSenderName) {
        this.emailSenderName = emailSenderName;
    }

    public String getEmailVerifyUri() {
        return emailVerifyUri;
    }

    public void setEmailVerifyUri(String emailVerifyUri) {
        this.emailVerifyUri = emailVerifyUri;
    }

}
