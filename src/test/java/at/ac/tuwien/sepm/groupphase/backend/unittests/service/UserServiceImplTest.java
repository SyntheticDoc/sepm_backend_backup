package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"));

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager txm;

    @Autowired
    PasswordEncoder passwordEncoder;

    TransactionStatus txstatus;

    @BeforeAll
    void setUp() {
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUpOne() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txstatus = txm.getTransaction(def);
        assumeTrue(txstatus.isNewTransaction());
        txstatus.setRollbackOnly();
    }

    @AfterEach
    void tearDown() {
        txm.rollback(txstatus);
    }

    @Test
    @DisplayName("Deleting own user should work.")
    public void deleteOwnUser() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            regUser.username(),
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        userService.deleteUser(regUser.username(), auth);

        assertEquals(0, userRepository.findAll().size(), "User must be correctly deleted");
    }

    @Test
    @DisplayName("Deleting other non-admin user as admin should work.")
    public void deleteOtherUserAsAdmin() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);
        var user = userRepository.findAll().get(0);
        user.setRoles(Stream.of("ROLE_USER").collect(Collectors.toSet()));
        userRepository.save(user);

        var auth = new UsernamePasswordAuthenticationToken(
            "adminuser",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
        );
        userService.deleteUser(regUser.username(), auth);

        assertEquals(0, userRepository.findAll().size(), "User must be correctly deleted");
    }

    @Test
    @DisplayName("Deleting other non-admin user as non-admin should throw AccessDeniedException.")
    public void deleteOtherUserAsNonAdmin() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);
        var user = userRepository.findAll().get(0);
        user.setRoles(Stream.of("ROLE_USER").collect(Collectors.toSet()));
        userRepository.save(user);

        var auth = new UsernamePasswordAuthenticationToken(
            "adminuser",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(regUser.username(), auth));
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Deleting other admin user as admin should throw AccessDeniedException.")
    public void deleteOtherAdminAsAdmin() {
        var regUser = new UserAccountDataDto("adminuser2", null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);
        var user = userRepository.findAll().get(0);
        user.setRoles(Stream.of("ROLE_USER", "ROLE_ADMIN").collect(Collectors.toSet()));
        userRepository.save(user);

        var auth = new UsernamePasswordAuthenticationToken(
            "adminuser",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
        );
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(regUser.username(), auth));
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Deleting non-existing user should throw NotFoundException.")
    public void deleteOtherNonExistingUser() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            "adminuser",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
        );
        assertThrows(NotFoundException.class, () -> userService.deleteUser(regUser.username() + "2", auth));
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Updating username should work.")
    public void updateUserUsername() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            regUser.username(),
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        var change = new UserAccountDataUpdateDto(VALID_USERNAME + "2", null, null);
        userService.updateUser(change, Locale.ENGLISH, auth);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(change.username(), userRepository.findAll().get(0).getUsername());
    }

    @Test
    @DisplayName("Updating e-mail should work.")
    public void updateUserEmail() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            regUser.username(),
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        var change = new UserAccountDataUpdateDto(null, VALID_EMAIL + "ok", null);
        userService.updateUser(change, Locale.ENGLISH, auth);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(change.email(), userRepository.findAll().get(0).getEmail());
    }

    @Test
    @DisplayName("Updating password should work.")
    public void updateUserPassword() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            regUser.username(),
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        var change = new UserAccountDataUpdateDto(null, null, VALID_PASSWORD + "2");
        userService.updateUser(change, Locale.ENGLISH, auth);
        assertEquals(1, userRepository.findAll().size());
        var user = userRepository.findAll().get(0);
        assertNotEquals(change.password(), user.getPassword());
        assertTrue(passwordEncoder.matches(change.password(), user.getPassword()));
    }

    @Test
    @DisplayName("Finding own user should work.")
    public void findOwnUser() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            regUser.username(),
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        var find = new UserFindDto(regUser.username(), null);
        var users = userService.findUsers(find, auth);
        assertEquals(1, users.size());
        assertEquals(regUser.username(), users.get(0).getUsername());
    }

    @Test
    @DisplayName("Finding other users as admin should work.")
    public void findOtherUserAsAdmin() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            "adminuser",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
        );
        var find = new UserFindDto(regUser.username(), null);
        var users = userService.findUsers(find, auth);
        assertEquals(1, users.size());
        assertEquals(regUser.username(), users.get(0).getUsername());
    }

    @Test
    @DisplayName("Finding other users as user should not return results.")
    public void findOtherUserAsUser() {
        var regUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, regUser);

        var auth = new UsernamePasswordAuthenticationToken(
            VALID_USERNAME + "2",
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        var find = new UserFindDto(regUser.username(), null);
        var users = userService.findUsers(find, auth);
        assertEquals(0, users.size());
    }

}
