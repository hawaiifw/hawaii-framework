package org.hawaiiframework.util;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.util.predicate.EmailAddressPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to obfuscate email addresses.
 */
public final class EmailUtil {

    /**
     * The predicate that checks whether an email address is conform RFC822.
     */
    private static final EmailAddressPredicate PREDICATE = new EmailAddressPredicate();

    /**
     * The logger.
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);
    /**
     * Minimal length of the user part (part before @) for which the first two characters are kept intact.
     */
    private static final int MIN_OBFUSCATION_LENGTH = 3;

    /**
     * The constructor.
     */
    private EmailUtil() {
        // private constructor for utility class.
    }

    /**
     * Returns @code{true} if the string @code{emailAddress} validates as an email address.
     * <p>
     * This method delegates to {@link EmailAddressPredicate}.
     */
    public static boolean isEmailAddress(final String emailAddress) {
        return PREDICATE.test(emailAddress);
    }

    /**
     * Obfuscate an email address.
     * <p>
     * <pre>
     * EmailUtil.obfuscate(null)                         = null
     * EmailUtil.obfuscate("")                           = ""
     * EmailUtil.obfuscate("john.doe@test.com")          = "jo***@test.com"
     * EmailUtil.obfuscate("john.doe@test.center.com")   = "jo***@test.center.com"
     * EmailUtil.obfuscate("jo@test.com")                = "***@test.com"
     * </pre>
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static String obfuscate(final String emailAddress) {
        if (StringUtils.isEmpty(emailAddress)) {
            return emailAddress;
        }
        LOGGER.debug("Obfuscating emailaddress {}", emailAddress);

        //First split user and domain part
        final String[] userDomain = emailAddress.split("@");
        final String user = userDomain[0];
        final String domain = userDomain[1];

        final StringBuilder builder = new StringBuilder();
        if (user.length() >= MIN_OBFUSCATION_LENGTH) {
            //Take the first two characters of the user part
            builder.append(StringUtils.substring(user, 0, 2));
        }
        builder.append("***@").append(domain);

        return builder.toString();
    }
}
