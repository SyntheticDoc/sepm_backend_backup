package at.ac.tuwien.sepm.groupphase.backend;

import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.Result;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.DisorderDataHandler;
import at.ac.tuwien.sepm.groupphase.backend.repository.Argon2ParametersRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ResultRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityDataRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.ResultServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
public class BackendApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResultServiceImpl resultService;
    private final ResultRepository resultRepository;
    private final Argon2PasswordEncoderWithParams argon2PasswordEncoder;
    private final SecurityDataRepository securityDataRepository;
    private final Argon2ParametersRepository argon2ParametersRepository;

    public BackendApplication(ResultServiceImpl resultService, ResultRepository resultRepository,
                              Argon2PasswordEncoderWithParams argon2PasswordEncoder,
                              SecurityDataRepository securityDataRepository,
                              Argon2ParametersRepository argon2ParametersRepository) {
        this.resultService = resultService;
        this.resultRepository = resultRepository;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
        this.securityDataRepository = securityDataRepository;
        this.argon2ParametersRepository = argon2ParametersRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /*
        Bean for getting and interpreting command line arguments handed via -Dspring-boot.run.arguments="##ARGUMENT##"

        If one of the arguments is parse-data_replace or parse-data_skip, the command line runner stops further evaluation
        of arguments at this point!
     */
    @Bean
    public CommandLineRunner commandLineRunnerBean() {
        return (args) -> {
            for (String arg : args) {
                LOGGER.info("Starting with command line argument: " + arg);
                if (arg.equals("parse-data_replace")) {
                    DisorderDataHandler.init(true);
                    break;
                } else if (arg.equals("parse-data_skip")) {
                    DisorderDataHandler.init(false);
                    break;
                }
            }
        };
    }

    private void testResultService() {
        Result r1 = new Result();
        Result r2 = new Result();
        Result r3 = new Result();

        r1.setValue("r1");
        r2.setValue("r2");
        r3.setValue("r3");

        Result r4 = new Result();
        Result r5 = new Result();

        r4.setValue("r4");
        r5.setValue("r5");

        String userPw1 = "userPw1";

        SecurityData saltData = securityDataRepository.findByType("saltMapping");
        Argon2Parameters params = argon2ParametersRepository.findByType("mapping");

        String userHash1 = argon2PasswordEncoder.encode(userPw1, params, saltData.getValue());

        r1.setUserHash(userHash1);
        r2.setUserHash(userHash1);
        r3.setUserHash(userHash1);

        String userPw2 = "userPw2";

        String userHash2 = argon2PasswordEncoder.encode(userPw2, params, saltData.getValue());

        r4.setUserHash(userHash2);
        r5.setUserHash(userHash2);

        resultRepository.deleteAll();

        resultRepository.saveAndFlush(r1);
        resultRepository.saveAndFlush(r2);
        resultRepository.saveAndFlush(r3);
        resultRepository.saveAndFlush(r4);
        resultRepository.saveAndFlush(r5);

        String res1 = "";

        for (Result r : resultService.getResultsForUser(userPw1)) {
            res1 += "\n" + r.getValue();
        }

        String res2 = "";

        for (Result r : resultService.getResultsForUser(userPw2)) {
            res2 += "\n" + r.getValue();
        }

        logThis(res1);
        logThis(res2);
    }

    private void logThis(String s) {
        String disp = "################################################";

        LOGGER.info("\n\n" + disp + "\n" + s + "\n\n" + disp + "\n");
    }

}
