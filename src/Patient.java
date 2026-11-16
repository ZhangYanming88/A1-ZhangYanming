import java.util.regex.Pattern;

/**
 * A patient of the health service.
 *
 * <p>Part 4 asks whether a patient should be its own class. It is, for three reasons: a patient
 * exists independently of any appointment, the same patient can hold several appointments, and
 * the rule that decides whether a phone number is valid belongs to the patient rather than to
 * every appointment that happens to mention one. Modelling the patient separately means the
 * phone validation is written once and can never be bypassed.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class Patient {

    /** A mobile number is exactly ten digits once formatting characters are removed. */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("\\d{10}");

    /** Characters callers may legitimately include when typing a phone number. */
    private static final Pattern SEPARATORS = Pattern.compile("[\\s\\-()]");

    /** Full name of the patient. */
    private final String name;

    /** Mobile number, stored as ten digits with no formatting. */
    private final String mobileNumber;

    /**
     * Creates a patient.
     *
     * @param name         the patient's name, must not be blank
     * @param mobileNumber the mobile number; spaces, hyphens and brackets are ignored,
     *                     but ten digits are required
     * @throws IllegalArgumentException if the name is blank or the number is not ten digits
     */
    public Patient(String name, String mobileNumber) {
        this.name = Validation.requireText(name, "A patient name");
        this.mobileNumber = normaliseMobile(mobileNumber);
    }

    /**
     * Returns the patient's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mobile number as ten unformatted digits.
     *
     * <p>Storing the canonical form means lookups never fail because one appointment recorded
     * {@code "0412 345 678"} and another recorded {@code "0412345678"}.</p>
     *
     * @return the ten digit mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Returns the mobile number in a readable grouping.
     *
     * @return for example {@code "0412 345 678"}
     */
    public String getFormattedMobileNumber() {
        return formatMobile(mobileNumber);
    }

    /**
     * Groups a canonical mobile number for display.
     *
     * @param canonicalDigits ten unformatted digits
     * @return for example {@code "0412 345 678"}
     */
    public static String formatMobile(String canonicalDigits) {
        return canonicalDigits.substring(0, 4) + " "
                + canonicalDigits.substring(4, 7) + " "
                + canonicalDigits.substring(7);
    }

    /**
     * Builds the printed representation of this patient.
     *
     * @return the name followed by the formatted mobile number
     */
    @Override
    public String toString() {
        return name + " (" + getFormattedMobileNumber() + ")";
    }

    /**
     * Compares this patient with another object.
     *
     * <p>Two patients are the same person when they share a mobile number: names can repeat, a
     * mobile number does not.</p>
     *
     * @param other the object to compare against
     * @return {@code true} if both objects describe the same patient
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Patient)) {
            return false;
        }
        Patient that = (Patient) other;
        return mobileNumber.equals(that.mobileNumber);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the mobile number
     */
    @Override
    public int hashCode() {
        return mobileNumber.hashCode();
    }

    /**
     * Removes formatting characters from a phone number and validates the result.
     *
     * @param mobileNumber the number as supplied by the caller
     * @return the ten digit canonical form
     * @throws IllegalArgumentException if the number is missing or is not ten digits
     */
    public static String normaliseMobile(String mobileNumber) {
        String digits = Validation.requireText(mobileNumber, "A patient mobile number")
                .replaceAll(SEPARATORS.pattern(), "");
        if (!MOBILE_PATTERN.matcher(digits).matches()) {
            throw new IllegalArgumentException(
                    "A patient mobile number must contain exactly ten digits, but was: "
                            + mobileNumber);
        }
        return digits;
    }
}
