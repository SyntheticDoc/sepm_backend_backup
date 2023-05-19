package at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Base64;

@SuppressWarnings("EnhancedSwitchMigration")
@Component
public class Argon2PasswordEncoderLogin extends Argon2PasswordEncoderWithParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String saltLogin;
    private final Argon2Parameters argon2Parameters;

    public Argon2PasswordEncoderLogin(SecurityDataRepository securityDataRepository,
                                      Argon2ParametersRepository argon2ParametersRepository) {

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
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            LOGGER.warn("Argon2PasswordEncoderLogin.matches(): password hash is null");
            return false;
        } else {
            byte[] decoded = Base64.getDecoder().decode(encodedPassword);

            byte[] hashBytes = new byte[decoded.length];
            Argon2BytesGenerator generator = new Argon2BytesGenerator();

            int argonType;

            switch (argon2Parameters.getArgonType().toLowerCase()) {
                case "argon2d":
                    argonType = 0;
                    break;
                case "argon2i":
                    argonType = 1;
                    break;
                case "argon2id":
                    argonType = 2;
                    break;
                default:
                    throw new IllegalArgumentException("Argon2 type parameter " + argon2Parameters.getArgonType() + " not recognized!");
            }

            org.bouncycastle.crypto.params.Argon2Parameters newParams = new org.bouncycastle.crypto.params.Argon2Parameters
                .Builder(argonType)
                .withSalt(Base64.getDecoder().decode(saltLogin))
                .withParallelism(argon2Parameters.getParallelism())
                .withMemoryAsKB((int) argon2Parameters.getMemoryCost())
                .withIterations(argon2Parameters.getIterations())
                .build();

            generator.init(newParams);
            generator.generateBytes(rawPassword.toString().toCharArray(), hashBytes);

            boolean result = true;

            for (int i = 0; i < decoded.length; i++) {
                if (decoded[i] != hashBytes[i]) {
                    result = false;
                    break;
                }
            }

            return result;
        }
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return this.encode(rawPassword, argon2Parameters, saltLogin);
    }
}
