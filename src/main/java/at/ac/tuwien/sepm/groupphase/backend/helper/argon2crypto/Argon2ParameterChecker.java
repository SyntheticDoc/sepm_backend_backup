package at.ac.tuwien.sepm.groupphase.backend.helper.argon2crypto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class Argon2ParameterChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Argon2Parameters getParametersForExecutionTime(int maxMilliseconds, int saltLength, int hashLength,
                                                          int parallelismDivider, int curMemDivider, int startIterCount) {
        LOGGER.info("Starting search for Argon2 parameters for the current machine.");
        LOGGER.info("Current maximum working memory available: " + getAvailableMemoryInKibi());
        LOGGER.info("Current maximum logical threads available: " + getLogicalThreads());

        long maxMem = getAvailableMemoryInKibi() / curMemDivider;

        int curIterations = startIterCount;
        int parallelism = getLogicalThreads() / parallelismDivider;
        long memoryIncreaseStep = maxMem / 2;

        long loopTimeElapsed = 0;
        long loopCount = 0;
        long timeNeeded = 0;

        // How much iterations should the search loop use at most?
        long maxLoopCount = 100;

        // How long should the parameter search run in seconds?
        long maxLoopTimeElapsed = 60;

        Argon2PasswordEncoder encoder;

        Instant startLoop = Instant.now();
        Instant startIter;
        Instant endIter;

        Argon2Parameters curParams = new Argon2Parameters();
        curParams.setArgonType("Argon2id");
        curParams.setSaltLength(saltLength);
        curParams.setHashLength(hashLength);
        curParams.setParallelism(parallelism);
        curParams.setMemoryCost(maxMem);
        curParams.setIterations(curIterations);

        Argon2Parameters lastParams = new Argon2Parameters(curParams);

        String testPw = "SupercomplicatedMegaSecurePassword";

        boolean switchMode = false;

        while (true) {
            loopCount++;
            Instant curLoopTime = Instant.now();
            loopTimeElapsed = ChronoUnit.SECONDS.between(startLoop, curLoopTime);

            if (loopCount > maxLoopCount) {
                LOGGER.error("Searching for argon2 parameters used maximum allowed search iterations! Current maximum: " + maxLoopCount);
                return null;
            } else if (loopTimeElapsed > maxLoopTimeElapsed) {
                LOGGER.error("Searching for argon2 parameters used the maximum allowed search time! Current maximum: " + maxLoopTimeElapsed);
                return null;
            }

            encoder = new Argon2PasswordEncoder(
                curParams.getSaltLength(),
                curParams.getHashLength(),
                curParams.getParallelism(),
                (int) curParams.getMemoryCost(),
                curParams.getIterations()
            );

            LOGGER.debug("Trying to find Argon2 parameters, current iteration: " + loopCount);
            LOGGER.debug("curMem: " + curParams.getMemoryCost() + ", curIter: " + curParams.getIterations());

            startIter = Instant.now();

            encoder.encode(testPw);

            endIter = Instant.now();

            timeNeeded = ChronoUnit.MILLIS.between(startIter, endIter);

            LOGGER.debug("Time needed: " + timeNeeded + " ms");

            if (!switchMode) {
                // Start search by reducing iterations and memory until faster than maxMilliseconds

                if (timeNeeded > maxMilliseconds) {
                    if (curIterations > 1) {
                        // Time too long, reduce iteration count
                        curIterations--;
                        lastParams = new Argon2Parameters(curParams);
                        curParams.setIterations(curIterations);
                    } else {
                        // Iterations down to 1, still takes too long, reduce memory
                        if (curParams.getMemoryCost() > 1024) {
                            if (timeNeeded > (maxMilliseconds / 3)) {
                                lastParams = new Argon2Parameters(curParams);
                                curParams.setMemoryCost(curParams.getMemoryCost() / 4);
                            } else {
                                lastParams = new Argon2Parameters(curParams);
                                curParams.setMemoryCost(curParams.getMemoryCost() / 2);
                            }
                        } else {
                            LOGGER.error("Searching for argon2 parameters can't reduce memory further - try to increase the available time");
                            return null;
                        }
                    }
                } else {
                    // Switch mode to increase memory again to get closer to maxMilliseconds
                    memoryIncreaseStep = curParams.getMemoryCost() / 10;
                    switchMode = true;
                }
            } else {
                // Increase memory again to get as close as possible to maxMilliseconds

                if (timeNeeded <= maxMilliseconds) {
                    lastParams = new Argon2Parameters(curParams);
                    curParams.setMemoryCost(curParams.getMemoryCost() + memoryIncreaseStep);
                } else {
                    // Good parameters found, return last parameter set already in memory, because it was <= maxMilliseconds
                    return lastParams;
                }
            }
        }
    }

    public void printMachineStats() {
        String s = "SYSTEM STATS\n\tAvailable Memory in Kibi: " + getAvailableMemoryInKibi() + "\n\tAvailable logical cores: " + getLogicalThreads();
        LOGGER.info("\n\n###################################\n\t" + s + "\n###################################\n");
    }

    public byte[] getNewSalt(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[length];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private long getAvailableMemoryInKibi() {
        return (long) (Runtime.getRuntime().maxMemory() * 0.001);
    }

    private int getLogicalThreads() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor cpu = hal.getProcessor();

        return cpu.getLogicalProcessorCount();
    }
}
