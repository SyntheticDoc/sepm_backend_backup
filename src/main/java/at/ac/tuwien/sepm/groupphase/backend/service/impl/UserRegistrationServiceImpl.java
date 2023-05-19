package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.RegistrationProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.exception.CollisionException;
import at.ac.tuwien.sepm.groupphase.backend.exception.PersistenceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ProcessException;
import at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository repo;
    private final Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams;
    private final String saltLogin;
    private final Argon2Parameters argon2Parameters;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final JwtTokenizer jwtTokenizer;
    private final MessageSource messageSource;
    private final RegistrationProperties regProperties;

    @Value("${application.registration.emailVerifyUri}")
    private String emailVerifyUri;

    @Autowired
    public UserRegistrationServiceImpl(UserRepository repo,
                                       Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams,
                                       JavaMailSender mailSender,
                                       SpringTemplateEngine templateEngine,
                                       JwtTokenizer jwtTokenizer,
                                       MessageSource messageSource,
                                       RegistrationProperties regProperties,
                                       SecurityDataRepository securityDataRepository,
                                       Argon2ParametersRepository argon2ParametersRepository) {
        this.repo = repo;
        this.argon2PasswordEncoderWithParams = argon2PasswordEncoderWithParams;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.jwtTokenizer = jwtTokenizer;
        this.messageSource = messageSource;
        this.regProperties = regProperties;

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
    @Transactional
    public String registerUser(Locale locale, UserAccountDataDto registerDto) {
        LOGGER.debug("Registering user: " + registerDto);
        var roles = SecurityConfig.REGISTER_ROLES
            .stream()
            .map(Object::toString)
            .collect(Collectors.toSet());
        ApplicationUser user = new ApplicationUser(
            null,
            registerDto.username().toLowerCase(),
            registerDto.email() == null ? null : registerDto.email().toLowerCase(),
            argon2PasswordEncoderWithParams.encode(registerDto.password(), argon2Parameters, saltLogin),
            roles,
            null,
            true,
            false
        );
        if (registerDto.email() != null) {
            final String loginKey = this.generateLoginKey();
            try {
                this.sendVerificationEmail(locale, loginKey, user);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new ProcessException(
                    "Sending e-mail failed",
                    FrontendErrorCode.REGISTER_VERIFYEMAIL_FAILED,
                    e
                );
            }
            user.setLoginKey(loginKey);
            user.setEnabled(false);
        }
        try {
            repo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CollisionException(
                "Username or e-mail already exist.",
                FrontendErrorCode.REGISTER_VALIDATION_USERNAME_OR_EMAIL_EXISTS,
                e
            );
        } catch (RuntimeException e) {
            throw new PersistenceException(
                "Failed to register new user.",
                FrontendErrorCode.REGISTER_GENERIC_FAILED,
                e
            );
        }

        if (user.getEnabled()) {
            return jwtTokenizer.getAuthToken(user.getUsername(), roles.stream().toList());
        }
        return null;
    }

    public void sendVerificationEmail(Locale locale, String loginKey, ApplicationUser user)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name());

        final String hrefUrl = emailVerifyUri + loginKey;

        Context context = new Context();
        context.setVariable("name", user.getUsername());
        context.setVariable("hrefUrl", hrefUrl);
        context.setLocale(locale);

        String toAddress = user.getEmail();
        String fromAddress = regProperties.getEmailSenderAddress();
        String senderName = regProperties.getEmailSenderName();

        String html = templateEngine.process("Verification_Mail_Template", context);
        helper.setTo(toAddress);
        helper.setText(html, true);
        helper.setSubject(messageSource.getMessage("register.verify_email.subject", null, locale));
        helper.setFrom(fromAddress, senderName);
        mailSender.send(message);
    }

    public String generateLoginKey() {
        Random random = new SecureRandom();
        byte[] randomData = new byte[SecurityConfig.LOGIN_KEY_BYTES];
        random.nextBytes(randomData);

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(randomData);
    }

}