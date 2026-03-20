package com.server.core.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDClock;
import org.apache.commons.lang3.IntegerRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.server.core.constants.CommonConstants.DEFAULT_ID_LENGTH;
import static com.server.core.constants.CommonConstants.EMPTY_STRING;
import static com.server.core.constants.CommonConstants.HYPHEN;
import static com.server.core.constants.CommonConstants.SECURE_RANDOM_ALGORITHM;

/**
 * @author Kazi Tanvir Azad
 */
public final class CommonUtil {
    private static final Logger log = LogManager.getLogger(CommonUtil.class);
    public static final IntegerRange SERVER_PORT_RANGE = IntegerRange.of(1, 65535);
    public static final IntegerRange RESPONSE_CODE_RANGE = IntegerRange.of(100, 599);

    private CommonUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    /**
     * Generates a unique alphanumeric key based on {@link SecureRandom} of the default length<br>
     *
     * @return {@link Optional}<{@link String}> Returns an Optional of unique alphanumeric key
     */
    public static Optional<String> generateUniqueAlphanumericId() {
        return generateUniqueAlphanumericId(DEFAULT_ID_LENGTH);
    }

    /**
     * Generates a unique alphanumeric key based on UUID version 7.<br>
     * This method first generates UUID version 7, then it removes the hyphen and converts the string to upper case.<br>
     *
     * @return {@link Optional}<{@link String}> Returns an Optional of unique alphanumeric key based on UUID version 7
     */
    public static Optional<String> generateUUID7BasedId() {
        Optional<String> uuid7 = generateUUID7();
        return uuid7.map(uuid7String -> uuid7String.replace(HYPHEN, EMPTY_STRING))
                .map(String::toUpperCase);
    }

    /**
     * Generates UUID version 7<br>
     *
     * @return Returns {@link UUID} in {@link Optional}<{@link String}> format
     */
    private static Optional<String> generateUUID7() {
        try {
            UUID uuid7 = Generators.timeBasedEpochRandomGenerator(getRandom(), UUIDClock.systemTimeClock()).generate();
            return Optional.ofNullable(uuid7.toString());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Generates a unique alphanumeric key based on {@link SecureRandom} of the specified length<br>
     *
     * @param idLength {@code int} Specify the length of the unique id to be generated
     * @return {@link Optional}<{@link String}> Returns an Optional of unique alphanumeric key
     */
    public static Optional<String> generateUniqueAlphanumericId(int idLength) {
        var upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        var numbers = "1234567890";
        var alphanumeric = upperCaseChars + lowerCaseChars + numbers;
        try {
            Random random = getRandom();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < idLength; i++) {
                int index = random.nextInt(alphanumeric.length());
                stringBuilder.append(alphanumeric.charAt(index));
            }
            return Optional.of(stringBuilder.toString());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Returns a SecureRandom object that implements the 'SHA1PRNG' algorithm<br>
     *
     * @return {@link Random}
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a {@code SecureRandomSpi}<br>
     *                                  implementation for the specified algorithm
     */
    private static Random getRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
    }

    /**
     * Securely generates a random number within the given range<br>
     *
     * @param min {@code int} Minimum number in the range
     * @param max {@code int} Maximum number in the range
     * @return {@code int} Random number within the given range
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a {@code SecureRandomSpi}<br>
     *                                  implementation for the specified algorithm
     */
    public static int getRandomNumberInRange(int min, int max) throws NoSuchAlgorithmException {
        return getRandom().nextInt(max - min) + min;
    }
}
