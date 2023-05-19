package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.security.token.LoginKeyAuthenticationToken;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Looks up authentication for a given loginKey and
 * returns a standard {@link UsernamePasswordAuthenticationToken}
 * for further auth.
 */
public class LoginKeyAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(LoginKeyAuthenticationToken.class, authentication,
            () -> messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.onlySupports",
                "Only LoginKeyAuthenticationToken is supported"));

        var user = ((CustomUserDetailService) this.getUserDetailsService())
            .loadUserByLoginKey((String) authentication.getCredentials());

        return new UsernamePasswordAuthenticationToken(
            user,
            user.getPassword()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LoginKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
