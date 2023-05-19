package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.CollisionException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Locale;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRegistrationServiceImplTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"));

    @Autowired
    UserRegistrationService userRegistrationService;

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
    @DisplayName("Registration should not allow duplicate username")
    public void registrationAddUserThatAlreadyExists() {
        UserAccountDataDto newUser = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, newUser);
        assertThrows(CollisionException.class, () -> userRegistrationService.registerUser(Locale.ENGLISH, newUser));
    }

    @Test
    @DisplayName("Registration should not allow duplicate e-mail")
    public void registrationAddEmailThatAlreadyExists() {
        UserAccountDataDto newUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, newUser);
        UserAccountDataDto newUser2 = new UserAccountDataDto(VALID_USERNAME + "2", VALID_EMAIL, VALID_PASSWORD);
        assertThrows(CollisionException.class, () -> userRegistrationService.registerUser(Locale.ENGLISH, newUser2));
    }

    @Test
    @DisplayName("Registration should encode password corretly")
    public void registrationShouldEncodePasswordCorrectly() {
        UserAccountDataDto newUser = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        userRegistrationService.registerUser(Locale.ENGLISH, newUser);

        assertEquals(1, userRepository.findAll().size(), "After registration, one user should be persisted");

        var user = userRepository.findAll().get(0);
        assertNotNull(user.getPassword(), "Password must not be null");
        assertNotEquals(newUser.password(), user.getPassword(), "Password must not be plain text");

        var passwordMatches = passwordEncoder.matches(newUser.password(), user.getPassword());
        assertTrue(passwordMatches, "Password encoder must be able to check raw password with persisted one");
    }


}
