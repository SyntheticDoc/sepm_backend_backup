package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.exception.CollisionException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.PersistenceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ProcessException;
import at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final UserRegistrationService registrationService;
    private final Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams;
    private final String saltLogin;
    private final Argon2Parameters argon2Parameters;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository,
                                   UserRegistrationService registrationService,
                                   Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams,
                                   SecurityDataRepository securityDataRepository,
                                   Argon2ParametersRepository argon2ParametersRepository) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
        this.argon2PasswordEncoderWithParams = argon2PasswordEncoderWithParams;

        SecurityData secData = securityDataRepository.findByType("saltLogin");

        if (secData != null) {
            saltLogin = secData.getValue();
        } else {
            saltLogin = null;
            LOGGER.error("saltLogin in UserRegistrationServiceImpl is null");
        }

        argon2Parameters = argon2ParametersRepository.findByType("login");

        if (argon2Parameters == null) {
            LOGGER.error("argon2Parameters in UserRegistrationServiceImpl is null");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        LOGGER.debug("Load user by email or username");
        try {
            ApplicationUser user = findApplicationUserByEmailOrUsername(emailOrUsername);
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(user.getRoles().toArray(new String[]{}));

            return new User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                !user.getLocked(),
                grantedAuthorities
            );
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Look up a user by their login key and set the key to null
     * so no further login attenmpts can be made.
     *
     * @param loginKey login key to look for
     * @return user details of the user found; or null if none was found
     * @throws BadCredentialsException if the login key does not exist
     */
    public UserDetails loadUserByLoginKey(String loginKey) throws BadCredentialsException {
        LOGGER.debug("Load user by login key");
        ApplicationUser user = userRepository.findUserByLoginKey(loginKey);
        if (user == null) {
            throw new BadCredentialsException("Login key is invalid.");
        } else if (!user.getEnabled()) {
            user.setEnabled(true);
        }
        user.setLoginKey(null);
        userRepository.save(user);

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(user.getRoles().toArray(new String[]{}));
        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    @Override
    public ApplicationUser findApplicationUserByEmailOrUsername(String emailOrUsername) {
        LOGGER.debug("Find application user by email or username");
        ApplicationUser applicationUser = userRepository.findUserByEmailOrUsername(emailOrUsername);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the username or email address %s", emailOrUsername));
    }

    @Override
    public void deleteUser(String username, Authentication auth) {
        LOGGER.debug("Request application user '{}' deletion by '{}'", username, auth.getName());
        var targetUser = this.userRepository.findUserByUsername(username);
        if (targetUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the username '%s'", username));
        }
        if (!username.equals(auth.getName())) { // want to delete other user
            var userIsAdmin = auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
            var targetUserIsAdmin = targetUser.getRoles().contains("ROLE_ADMIN");
            if (!userIsAdmin || targetUserIsAdmin) {
                throw new AccessDeniedException("You have insufficient permissions to delete this user.");
            }
        }
        this.userRepository.delete(targetUser);
        LOGGER.debug("Application user '{}' successfully deleted", username);
    }

    @Override
    public void updateUser(UserAccountDataUpdateDto data, Locale locale, Authentication auth) {
        var username = auth.getName();
        LOGGER.debug("Update application user '{}'", username);
        var targetUser = userRepository.findUserByUsername(username);
        if (targetUser == null) {
            throw new AccessDeniedException("Your user does not exist anymore.");
        }
        if (data.username() != null) {
            targetUser.setUsername(data.username().toLowerCase());
        }
        if (data.email() != null) {
            targetUser.setEmail(data.email().toLowerCase());
            final String loginKey = registrationService.generateLoginKey();
            try {
                registrationService.sendVerificationEmail(locale, loginKey, targetUser);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new ProcessException(
                    "Sending e-mail failed",
                    FrontendErrorCode.REGISTER_VERIFYEMAIL_FAILED,
                    e
                );
            }
            targetUser.setLoginKey(loginKey);
            targetUser.setEnabled(false);
        }
        if (data.password() != null) {
            targetUser.setPassword(argon2PasswordEncoderWithParams.encode(data.password(), argon2Parameters, saltLogin));
        }

        try {
            userRepository.save(targetUser);
        } catch (DataIntegrityViolationException e) {
            throw new CollisionException(
                "Username or e-mail already exist.",
                FrontendErrorCode.USER_UPDATE_VALIDATION_USERNAME_OR_EMAIL_EXISTS,
                e
            );
        } catch (RuntimeException e) {
            throw new PersistenceException(
                "Failed to update user.",
                FrontendErrorCode.USER_UPDATE_GENERIC_FAILED,
                e
            );
        }
    }

    @Override
    public List<ApplicationUser> findUsers(UserFindDto findDto, Authentication auth) {
        LOGGER.debug("Find all users matching criteria: {} by user '{}'", findDto, auth.getName());
        var userIsAdmin = auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
        try {
            return userRepository.findUsers(findDto, true)
                .stream()
                .filter(user -> userIsAdmin || user.getUsername().equals(auth.getName()))
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new PersistenceException(
                "Failed to find users.",
                FrontendErrorCode.UNKNOWN,
                e
            );
        }
    }

    @Override
    public int findUserCount(UserFindDto findDto) {
        LOGGER.debug("Find user count matching criteria: {}", findDto);
        try {
            return userRepository.findUsers(findDto, true).size();
        } catch (RuntimeException e) {
            throw new PersistenceException(
                "Failed to find user count.",
                FrontendErrorCode.UNKNOWN,
                e
            );
        }
    }

}
