package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Locale;

/**
 * Service for user-related logic.
 */
public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param emailOrUsername the email address or username
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exist
     */
    @Override
    UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address or username.
     *
     * @param emailOrUsername the email address or username
     * @return an application user
     */
    ApplicationUser findApplicationUserByEmailOrUsername(String emailOrUsername);

    /**
     * Delete a single user by given username.
     * Every user is allowed to delete themselves, but only admin are allowed
     * to delete others (but not admins).
     *
     * @param username username of user to delete
     * @param auth current user authentication
     */
    void deleteUser(String username, Authentication auth);

    /**
     * Update the current users' account data.
     *
     * @param data data to be set
     * @param locale locale requested by user
     * @param auth current auth token
     */
    void updateUser(UserAccountDataUpdateDto data, Locale locale, Authentication auth);

    /**
     * Find users matching ANY of the given criteria.
     * If the current auth token has no access to view certain users,
     * these users are omitted and NOT returned.
     *
     * @param findDto criteria
     * @param auth current user auth
     * @return list of matching users
     */
    List<ApplicationUser> findUsers(UserFindDto findDto, Authentication auth);

    /**
     * Find how many users match ANY of the given criteria.
     *
     * @param findDto criteria
     * @return amount of users matching
     */
    int findUserCount(UserFindDto findDto);

}
