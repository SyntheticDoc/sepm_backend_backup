package at.ac.tuwien.sepm.groupphase.backend.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Token for login key only.
 */
public class LoginKeyAuthenticationToken extends AbstractAuthenticationToken {

    private String loginKey;

    public LoginKeyAuthenticationToken(String loginKey) {
        super(null);
        this.loginKey = loginKey;
    }

    @Override
    public Object getCredentials() {
        return this.loginKey;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
