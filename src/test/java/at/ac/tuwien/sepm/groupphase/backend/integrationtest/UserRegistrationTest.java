package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.config.properties.RegistrationProperties;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserRegistrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
        .withPerMethodLifecycle(true);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties secProperties;

    @Autowired
    private RegistrationProperties regProperties;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Registration with empty username should return bad request.")
    public void registrationWithEmptyUsernameShouldReturnBadRequest() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            "",
            null,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(0, userRepository.findAll().size(), "No user should be created when invalid registration data is given.");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    @DisplayName("Registration without e-mail should not create login key and send no e-mail.")
    public void registrationWithOutEmailShouldNotCreateLoginKeyAndSendNoEmail() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            null,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(1, userRepository.findAll().size(), "User should be created when valid registration data is given.");
        var newUser = userRepository.findAll().get(0);
        assertTrue(newUser.getEnabled(), "User should be enabled after registration without e-mail.");
        assertNull(newUser.getEmail(), "User should have no e-mail after registration without e-mail.");
        assertNull(newUser.getLoginKey(), "User should not have a login key when no e-mail is given.");
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registration without e-mail should create enabled user and return valid JWT.")
    public void registrationWithOutEmailShouldCreateEnabledUserAndReturnValidJWT() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            null,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(1, userRepository.findAll().size(), "User should be created when valid registration data is given.");
        var newUser = userRepository.findAll().get(0);
        assertTrue(newUser.getEnabled(), "User should be enabled after registration without e-mail.");

        var token = response.getContentAsString();
        byte[] signingKey = secProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token.replace(secProperties.getAuthTokenPrefix(), ""))
            .getBody();

        assertEquals(registerDto.username(), claims.getSubject(), "Returned JWT has different username than given");
        assertTrue(claims.containsKey("rol"), "Returned JWT has no roles");

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .toList();
        assertEquals(1, authorities.size(), "Returned JWT has more/less than 1 role");
        assertEquals("ROLE_USER", authorities.get(0).getAuthority(), "Returned JWT does not have USER role");
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN_VALUE, response.getContentType().substring(0, response.getContentType().indexOf(';')));
    }

    @Test
    @DisplayName("Registration with e-mail should create disabled user and return no JWT.")
    public void registrationWithEmailShouldCreateDisabledUserAndReturnNoJWT() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            VALID_EMAIL,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(1, userRepository.findAll().size(), "User should be created when valid registration data is given.");
        var newUser = userRepository.findAll().get(0);
        assertFalse(newUser.getEnabled(), "User should be disabled after registration with e-mail.");
        assertTrue(response.getContentAsString().isEmpty(), "Expected empty body");
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registration with empty e-mail should return bad request.")
    public void registrationWithEmptyEmailShouldReturnBadRequest() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            "",
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(0, userRepository.findAll().size(), "No user should be created when invalid registration data is given.");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registration with valid username/email/password without locale should send correct english activation email.")
    public void registerValidUserWithoutLocaleShouldSendCorrectEnglishEmail() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            VALID_EMAIL,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus(), "Registration must return HTTP 201");

        var user = userRepository.findAll().get(0);
        assertNotNull(user, "After registration, user must not be null");
        assertFalse(user.getEnabled(), "After registration with email, user must not be enabled");
        assertNotNull(user.getLoginKey(), "After registration with email, user login key must not be null");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            Context context = new Context();
            context.setVariable("name", VALID_USERNAME);
            context.setVariable("hrefUrl", regProperties.getEmailVerifyUri() + user.getLoginKey());
            context.setLocale(Locale.ENGLISH);

            var html = templateEngine.process("Verification_Mail_Template", context);
            var receivedHtml = new MimeMessageParser(receivedMessage).parse().getHtmlContent().replaceAll("\r", "");

            assertEquals(html, receivedHtml);
        });
    }

    @Test
    @DisplayName("Registration with valid username/email/password with german locale should send correct german activation email.")
    public void registerValidUserWithGermanLocaleShouldSendCorrectGermanEmail() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            VALID_EMAIL,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .locale(Locale.GERMAN)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(response.getStatus(), 201, "Registration must return HTTP 201");

        var user = userRepository.findAll().get(0);
        assertNotNull(user, "After registration, user must not be null");
        assertFalse(user.getEnabled(), "After registration with email, user must not be enabled");
        assertNotNull(user.getLoginKey(), "After registration with email, user login key must not be null");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            Context context = new Context();
            context.setVariable("name", VALID_USERNAME);
            context.setVariable("hrefUrl", regProperties.getEmailVerifyUri() + user.getLoginKey());
            context.setLocale(Locale.GERMAN);

            var html = templateEngine.process("Verification_Mail_Template", context);
            var receivedHtml = new MimeMessageParser(receivedMessage).parse().getHtmlContent().replaceAll("\r", "");

            assertEquals(html, receivedHtml);
        });
    }

    @Test
    @DisplayName("Registration with valid username/email/password with unknown locale should send correct english activation email.")
    public void registerValidUserWithUnknownLocaleShouldSendCorrectEnglishEmail() throws Exception {
        UserAccountDataDto registerDto = new UserAccountDataDto(
            VALID_USERNAME,
            VALID_EMAIL,
            VALID_PASSWORD
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(USER_BASE_URI)
                    .content(objectMapper.writeValueAsString(registerDto))
                    .locale(UNKNOWN_LOCALE)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(response.getStatus(), 201, "Registration must return HTTP 201");

        var user = userRepository.findAll().get(0);
        assertNotNull(user, "After registration, user must not be null");
        assertFalse(user.getEnabled(), "After registration with email, user must not be enabled");
        assertNotNull(user.getLoginKey(), "After registration with email, user login key must not be null");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            Context context = new Context();
            context.setVariable("name", VALID_USERNAME);
            context.setVariable("hrefUrl", regProperties.getEmailVerifyUri() + user.getLoginKey());
            context.setLocale(UNKNOWN_LOCALE);

            var html = templateEngine.process("Verification_Mail_Template", context);
            var receivedHtml = new MimeMessageParser(receivedMessage).parse().getHtmlContent().replaceAll("\r", "");

            assertEquals(html, receivedHtml);
        });
    }

}
