/**
 * Validation helpers shared by the domain classes.
 *
 * <p>The same three checks - "text must be present", "a number must be positive", "an object
 * must be supplied" - are needed by almost every class in the program. Collecting them here
 * means the rule is expressed once and every class rejects bad input with the same style of
 * message, instead of each class inventing its own wording and its own edge cases.</p>
 *
 * <p>This is a utility class: it holds no state and cannot be instantiated.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public final class Validation {

    /** Prevents instantiation of this utility class. */
    private Validation() {
    }

    /**
     * Ensures a piece of text is present and returns it trimmed.
     *
     * @param value     the candidate text
     * @param fieldName the field name used in the error message
     * @return the text, trimmed
     * @throws IllegalArgumentException if the text is {@code null} or blank
     */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }
        return value.trim();
    }

    /**
     * Ensures a number is greater than zero.
     *
     * @param value     the candidate number
     * @param fieldName the field name used in the error message
     * @return the same number
     * @throws IllegalArgumentException if the number is zero or negative
     */
    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero, but was: " + value);
        }
        return value;
    }
}
