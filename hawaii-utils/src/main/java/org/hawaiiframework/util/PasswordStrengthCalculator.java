package org.hawaiiframework.util;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * This class is a password strength calculator. It uses {@link Zxcvbn} to calculate the password strength.
 */
@Component
public class PasswordStrengthCalculator {

    /**
     * The actual strength calculator.
     */
    private static final Zxcvbn ZXCVBN = new Zxcvbn();

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordStrengthCalculator.class);

    /**
     * The configuration for the password strength checker.
     */
    private final PasswordStrengthConfiguration configuration;


    /**
     * An additional dictionary containing dutch words.
     */
    private List<String> dutchDictionary;

    /**
     * The constructor.
     */
    @Autowired
    public PasswordStrengthCalculator(final PasswordStrengthConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
    }

    /**
     * Check if a password too short or is weak.
     *
     * @param password the password to check
     * @return true if considered weak, false otherwise
     */
    public boolean isWeakPassword(final String password) {
        return isTooShort(password) || isWeak(measurePassword(password));
    }

    /**
     * Calls ZXCVBN to calculate the strength for a given password and returns a {@link PasswordStrength} created from this.
     *
     * @param password The password to get the strength of.
     * @return the PasswordStrength determined by ZXCVBN.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public PasswordStrength getPasswordStrength(final String password) {
        final Strength zxcvbnStrength = measurePassword(password);

        final PasswordStrength result = new PasswordStrength();
        result.setWeak(isWeakPassword(password));
        result.setStrength(zxcvbnStrength.getScore());
        result.setTooShort(isTooShort(password));

        return result;
    }

    private Strength measurePassword(final String password) {
        return ZXCVBN.measure(password, getDutchDictionary());
    }

    private boolean isTooShort(final String password) {
        return password.length() < configuration.getMinimumPasswordLength();
    }

    private boolean isWeak(final Strength strength) {
        return strength.getScore() < configuration.getMinimumPasswordStrength();
    }

    /**
     * Return the configured error code for too weak passwords.
     */
    public String getTooWeakErrorCode() {
        return configuration.getTooWeakErrorCode();
    }

    /**
     * Get the dutchDictionary.
     * If it has not been loaded yet, loads it from the resource file.
     *
     * @return The dutchDictionary.
     **/
    public List<String> getDutchDictionary() {
        if (dutchDictionary == null || dutchDictionary.isEmpty()) {
            loadDictionary();
        }
        return dutchDictionary;
    }

    /**
     * Loads the dutch dictionary from the resource file.
     */
    private void loadDictionary() {
        dutchDictionary = new ArrayList<>();
        final Properties properties = new Properties();
        try {
            @SuppressWarnings("PMD.LawOfDemeter") final InputStream dictionaryAsStream =
                    getClass().getClassLoader().getResourceAsStream(configuration.getDutchDictionaryFileName());
            properties.load(dictionaryAsStream);
            for (final Object key : properties.keySet()) {
                dutchDictionary.add((String) key);
            }
        } catch (IOException e) {
            LOGGER.debug("Error loading zxcvbn dictionary");
        }
    }
}
