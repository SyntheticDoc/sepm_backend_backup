package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

import java.util.List;
import java.util.Locale;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserLoginTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties secProperties;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Login without any data should fail.")
    public void loginWithoutData() throws Exception {
        registrationService.registerUser(Locale.ENGLISH, new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD));

        UserLoginDto loginDto = new UserLoginDto(
            null,
            null,
            null
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus(), response.getContentType());
        assertFalse(response.getContentAsString().startsWith(secProperties.getAuthTokenPrefix()));
    }

    @Test
    @DisplayName("Login with login key should enable previously disabled user.")
    public void loginWithLoginKeyEnable() throws Exception {

        registrationService.registerUser(Locale.ENGLISH, new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD));
        assertEquals(1, userRepository.findAll().size());
        var user = userRepository.findAll().get(0);

        UserLoginDto loginDto = new UserLoginDto(
            null,
            null,
            user.getLoginKey()
        );
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        user = userRepository.findAll().get(0);
        assertTrue(user.getEnabled(), "After login with login key, users should be enabled if they weren't previously.");
        assertNull(user.getLoginKey(), "After successful login, the login key should be set to null.");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login should return valid JWT.")
    public void loginWithUsernameValidJWT() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());

        UserLoginDto loginDto = new UserLoginDto(regDto.username(), regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var token = response.getContentAsString();
        byte[] signingKey = secProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token.replace(secProperties.getAuthTokenPrefix(), ""))
            .getBody();

        assertEquals(regDto.username(), claims.getSubject(), "Returned JWT has different username than given");
        assertTrue(claims.containsKey("rol"), "Returned JWT has no roles");

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .toList();
        assertEquals(1, authorities.size(), "Returned JWT has more/less than 1 role");
        assertEquals("ROLE_USER", authorities.get(0).getAuthority(), "Returned JWT does not have USER role");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login with valid username should return HTTP 200.")
    public void loginWithUsername() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, null, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());

        UserLoginDto loginDto = new UserLoginDto(regDto.username(), regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login disabled user should return HTTP 401.")
    public void loginDisabledUser() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());
        var user = userRepository.findAll().get(0);
        user.setEnabled(false);
        userRepository.save(user);

        UserLoginDto loginDto = new UserLoginDto(regDto.email(), regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login with valid e-mail should return HTTP 200.")
    public void loginWithEmail() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());
        var user = userRepository.findAll().get(0);
        user.setEnabled(true);
        userRepository.save(user);

        UserLoginDto loginDto = new UserLoginDto(regDto.email(), regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login with invalid username should return HTTP 401.")
    public void loginWithInvalidUsername() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());

        UserLoginDto loginDto = new UserLoginDto(regDto.username() + "2", regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertFalse(response.getContentAsString().startsWith(secProperties.getAuthTokenPrefix()));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login with invalid e-mail should return HTTP 401.")
    public void loginWithInvalidEmail() throws Exception {
        var regDto = new UserAccountDataDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        registrationService.registerUser(Locale.ENGLISH, regDto);
        assertEquals(1, userRepository.findAll().size());

        UserLoginDto loginDto = new UserLoginDto(regDto.email() + "2", regDto.password(), null);
        MvcResult mvcResult = this.mockMvc.perform(
                post(LOGIN_BASE_URI)
                    .content(objectMapper.writeValueAsString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertFalse(response.getContentAsString().startsWith(secProperties.getAuthTokenPrefix()));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

}
