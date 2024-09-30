package com.mylearning.journalapp.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * THIS IS MASKING FOR SENSITIVE DATA'S IN LOGS
 *
 * this LogEventPatternConverter is from log4j2
 * and @Plugin is used to register this class as plugin and inside @Plugin we specified name of the plugin with category of the log4j2 plugin we use
 * Log4j2 uses plugins like Appenders and Layouts to format and output logs.
 * There are 5 categories of log4j2 plugins:
 * 1. Core Plugins
 * 2. Convertors
 * 3. Key Providers
 * 4. Lookups
 * 5. Type Converters
 *
 * in @ConverterKeys whatever the value we put inside is that what we refer it in the patternlayout tag inside msg component of log4j2.xml file inside >> <PatternLayout pattern="${LOG_PATTERN}"/>
 *so this is the key which is going to be referred, and it is going to be replaced in the log messages so it will look for that particular key and then see the class
 * that this is the class it is associated with converter and it will do the masking.
 *
 */
@Slf4j
@Plugin(name = "LogMaskingConverter",category = "Converter")
@ConverterKeys({"passMask"})
public class MaskingConverter extends LogEventPatternConverter {


    /**
     * "pin: {}, cardNumber: {}",pin,cardNumber
     * log.info("UserController getUserByUsername UserDto :: userName : {}, password : {}, email : {}",userDto.userName(),userDto.password(),userDto.email());
     *
     * ^\$2[aby]?\$[0-9]{2}\$[./A-Za-z0-9]{53}$
     *
     * This pattern breaks down as follows:
     *
     * ^\$2[aby]?\$: Ensures the hash starts with $2a$, $2b$, or $2y$.
     * ^:Asserts the position at the start of the string. This ensures that the pattern must begin at the very start of the string.
     * \$: The backslash \ is an escape character, and $ is a special character in regex that usually denotes the end of a string. By escaping it with \, we tell the regex engine to treat $ as a literal dollar sign.
     * [aby]?
     * ?: This quantifier means that the preceding character class [aby] is optional. It matches 0 or 1 occurrence of a, b, or y. [aby]? matches either a, b, y, or nothing at all.
     * Again, the backslash \ escapes the dollar sign $, making it a literal dollar sign.
     *
     * [0-9]{2}\$: Matches the cost factor, which is a two-digit number followed by a $.
     * [./A-Za-z0-9]{53}$: Matches the 53-character hash.
     * $: Asserts the position at the end of the string. This ensures that the entire string must end after exactly 53 characters from the specified character class.
     * [./A-Za-z0-9] matches any single character that is a dot, forward slash, uppercase letter, lowercase letter, or digit.
     *
     * sample bycrypt hash :: for raju123 >> $2a$12$XbxEk0f1DAoNCz2vo/HVAu7bzTmeJeaF9XKqw8JjZrojAc3MEDxEq
     *
     * private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile("(pin:\\s*\\d{4})|(cardNumber:\\s*\\d{16})");
     *
     * MY FIRST ATTEMPT OF PASSWORD REGEX AND WHY IT FAILED FOR MATCHING ::: ??
     * password :\s*^\$2[aby]?\$[0-9]{2}\$[./A-Za-z0-9]{53}$
     *
     * Anchoring the string: You are using the ^ (start of the string) and $ (end of the string) anchors inside the regex,
     * which is unnecessary and incorrect for this use case, as the anchors should be placed at the boundaries of
     * the entire pattern, not inside it.
     *
     * EXPLANATION FOR BELOW PASSWORD : REGEX
     * password\\s*:\\s*: Matches password : where spaces are optional (\\s*), ensuring it matches even if there are extra spaces between password and : or after :.
     *
     * \\$2[aby]?\\$: Matches bcrypt's initial format, where $2a$, $2b$, or $2y$ are valid. This handles the start of a bcrypt hash.
     *
     * [0-9]{2}: Matches the cost factor (which is always 2 digits).
     *
     * \\$[./A-Za-z0-9]{53}: Matches the 53-character hash after the cost factor, consisting of letters, digits, periods (.), and slashes (/).
     *
     */
    //private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile("(password\\s*:\\s*\\$2[aby]?\\$[0-9]{2}\\$[./A-Za-z0-9]{53})");

     // while password logging below pattern can match patterns like >> password:xxxx, password : xxxxx, password=xxxx, password = xxxxx
    private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile("(password\\s*[:=]\\s*\\$2[aby]?\\$[0-9]{2}\\$[./A-Za-z0-9]{53})");

    // Updated regex to match "password : value"
    // \\S+ \\S matches any non-whitespace character.+ is a quantifier that matches 1 or more occurrences of the preceding element.
    // So, \\S+ matches one or more non-whitespace characters.
    //private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile("(password :\\s*\\S+)");

    protected MaskingConverter(String name, String style) {
        super(name, style);
    }

    public static MaskingConverter newInstance(String[] options) {
        log.info("MaskingConverter newInstance() called");
        return new MaskingConverter("msgMask123", "style");
    }

    /**
     * This method formats logging event and mask the sensitive data and append it to StringBuilder
     * @param event
     * @param toAppendTo
     */
    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        String logEventMsg = event.getMessage().getFormattedMessage();
        Matcher sensitiveMatcher = SENSITIVE_DATA_PATTERN.matcher(logEventMsg);
        StringBuffer sb = new StringBuffer();

        // Replace the password with asterisks if found
        /*while (sensitiveMatcher.find()){
            String replacement;
            if(sensitiveMatcher.group().contains("password")){
                replacement = "password : ********************************";
            }else{
                replacement = sensitiveMatcher.group();
            }
            sensitiveMatcher.appendReplacement(sb, replacement);
        }
        sensitiveMatcher.appendTail(sb);
        toAppendTo.append(sb);*/

        // Replace the password with asterisks if found
        while (sensitiveMatcher.find()) {
            //log.info("MaskingConverter inside while loop sb : {}",sb);
            sensitiveMatcher.appendReplacement(sb, "password : ********");
        }
        sensitiveMatcher.appendTail(sb);
        toAppendTo.append(sb.toString());
    }
}
