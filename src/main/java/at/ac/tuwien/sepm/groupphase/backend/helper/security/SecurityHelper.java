package at.ac.tuwien.sepm.groupphase.backend.helper.security;

import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto.Argon2ParameterChecker;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Base64;

@Component
public class SecurityHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Argon2ParameterChecker argon2ParameterChecker;
    private final Argon2ParametersRepository argon2ParametersRepository;
    private final SecurityDataRepository securityDataRepository;
    private final Environment env;

    private final int saltLength = 128;

    public SecurityHelper(Argon2ParameterChecker argon2ParameterChecker, Argon2ParametersRepository argon2ParametersRepository,
                          SecurityDataRepository securityDataRepository, Environment env) {
        this.argon2ParameterChecker = argon2ParameterChecker;
        this.argon2ParametersRepository = argon2ParametersRepository;
        this.securityDataRepository = securityDataRepository;
        this.env = env;
    }

    @PostConstruct
    public void checkArgon2Parameters() {
        LOGGER.info("Checking Argon2 parameters...");

        // If in development mode, use only a reduced set of parameters
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            useReducedArgon2Parameters();
            return;
        }

        if (argon2ParametersRepository.count() != 2) {
            LOGGER.info("Required parameters do not exist");
            getNewArgon2Parameters();
        } else {
            LOGGER.info("Argon2 login parameters found: " + argon2ParametersRepository.findByType("login"));
            LOGGER.info("Argon2 mapping parameters found: " + argon2ParametersRepository.findByType("mapping"));
        }

        LOGGER.info("Checking cryptographic salts...");

        if (securityDataRepository.findByType("saltLogin") == null) {
            LOGGER.info("No login salt value found. A new salt will now be generated. If you believe this is an error, please "
                + "shut down the server immediately and call your server administrator!");
            generateNewSalt("saltLogin");
        } else {
            LOGGER.info("\n\nSALT VALUE: " + securityDataRepository.findByType("saltLogin").getValue() + "\n\n");
        }

        if (securityDataRepository.findByType("saltMapping") == null) {
            LOGGER.info("No mapping salt value found. A new salt will now be generated. If you believe this is an error, please "
                + "shut down the server immediately and call your server administrator!");
            generateNewSalt("saltMapping");
        } else {
            LOGGER.info("\n\nSALT VALUE: " + securityDataRepository.findByType("saltMapping").getValue() + "\n\n");
        }
    }

    public void useReducedArgon2Parameters() {
        LOGGER.info("Creating reduced Argon2 parameters...");
        argon2ParametersRepository.deleteAll();
        argon2ParametersRepository.flush();
        argon2ParametersRepository.save(getStandardParameters("login"));
        argon2ParametersRepository.save(getStandardParameters("mapping"));
        argon2ParametersRepository.flush();

        String salt = "w4NqRcIeIfMBbM1KYGz4abkPepmf0K1pJPD6K/0KcpmdUz+1lMm89woNAs42Y9BuQmFS0JZbr9gyyr/jeuPcc9z36ZchYpI2+"
            + "+tDlSwmhPi2e1UrYt4gtQUHf5E0CfyeF60lLGFrpcinDWmRtdOCNYLIJJvNyFoVMjVfdXoUEaY=";

        SecurityData saltData1 = new SecurityData();
        saltData1.setType("saltLogin");
        saltData1.setValue(salt);

        SecurityData saltData2 = new SecurityData();
        saltData2.setType("saltMapping");
        saltData2.setValue(salt);

        securityDataRepository.deleteAll();
        securityDataRepository.flush();
        securityDataRepository.save(saltData1);
        securityDataRepository.save(saltData2);
        securityDataRepository.flush();

        LOGGER.info("Argon2 login parameters found: " + argon2ParametersRepository.findByType("login"));
        LOGGER.info("Argon2 mapping parameters found: " + argon2ParametersRepository.findByType("mapping"));
        LOGGER.info("Login salt: " + securityDataRepository.findByType("login").getValue());
        LOGGER.info("Mapping salt: " + securityDataRepository.findByType("mapping").getValue());
    }

    private void getNewArgon2Parameters() {
        LOGGER.info("Creating new Argon2 parameters...");

        argon2ParametersRepository.deleteAll();
        argon2ParametersRepository.flush();

        Argon2Parameters loginParams;

        LOGGER.info("Calculating Argon2 login parameters...");
        loginParams = argon2ParameterChecker.getParametersForExecutionTime(1000, saltLength, 128,
            3, 4, 2);

        Argon2Parameters mappingParams;

        LOGGER.info("Calculating Argon2 mapping parameters...");
        mappingParams = argon2ParameterChecker.getParametersForExecutionTime(3000, saltLength, 128,
            3, 4, 2);

        if (loginParams != null) {
            loginParams.setType("login");
            argon2ParametersRepository.save(loginParams);
        } else {
            LOGGER.error("Could not get new argon2 parameters for login! Setting standard parameters. WARNING: This is unsafe, "
                    + "please try again as soon as possible!");
            argon2ParametersRepository.save(getStandardParameters("login"));
        }

        if (mappingParams != null) {
            mappingParams.setType("mapping");
            argon2ParametersRepository.save(mappingParams);
        } else {
            LOGGER.error("Could not get new argon2 parameters for mapping! Setting standard parameters. WARNING: This is unsafe, "
                + "please try again as soon as possible!");
            argon2ParametersRepository.save(getStandardParameters("mapping"));
        }

        argon2ParametersRepository.flush();
    }

    private Argon2Parameters getStandardParameters(String type) {
        Argon2Parameters params = new Argon2Parameters();

        params.setType(type);
        params.setArgonType("Argon2id");
        params.setSaltLength(saltLength);
        params.setHashLength(128);
        params.setParallelism(1);
        params.setMemoryCost(256);
        params.setIterations(1);

        return params;
    }

    private void generateNewSalt(String saltType) {
        LOGGER.info("Generating new salt of type " + saltType + "...");
        byte[] salt = argon2ParameterChecker.getNewSalt(saltLength);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        SecurityData saltData = new SecurityData();
        saltData.setType(saltType);
        saltData.setValue(saltBase64);

        LOGGER.warn("\n#########################################\n\nYour salt value: " + saltBase64 + "\n\nPlease "
            + "write this value down at a very secure place. Without this value, all data in your database will be lost forever "
            + "if you lose it!\n\n#########################################");

        securityDataRepository.saveAndFlush(saltData);
    }
}
