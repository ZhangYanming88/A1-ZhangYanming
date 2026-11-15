import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The days of the week on which a health professional may be available.
 *
 * <p>An enumeration is used rather than free text for two reasons:</p>
 * <ul>
 *   <li>the set of days is a <em>fixed</em> collection of values, so an invalid value such as
 *       {@code "Funday"} becomes impossible to represent - the compiler rejects it;</li>
 *   <li>{@link EnumSet} provides a compact, type-safe and very fast representation of a
 *       subset of those values, which is exactly what "works on these days each week" is.</li>
 * </ul>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public enum Day {

    /** Monday. */
    MONDAY("Monday"),
    /** Tuesday. */
    TUESDAY("Tuesday"),
    /** Wednesday. */
    WEDNESDAY("Wednesday"),
    /** Thursday. */
    THURSDAY("Thursday"),
    /** Friday. */
    FRIDAY("Friday"),
    /** Saturday. */
    SATURDAY("Saturday"),
    /** Sunday. */
    SUNDAY("Sunday");

    /** Human readable name used when a professional's details are printed. */
    private final String displayName;

    /**
     * Creates a day constant.
     *
     * @param displayName the name to show to a reader of the program output
     */
    Day(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human readable name of this day.
     *
     * @return for example {@code "Monday"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Reports whether this day is a normal business weekday.
     *
     * @return {@code true} for Monday to Friday, {@code false} for the weekend
     */
    public boolean isWeekday() {
        return this != SATURDAY && this != SUNDAY;
    }

    /**
     * Creates a new, independent set containing Monday to Friday.
     *
     * <p>A fresh set is returned on every call so that callers can modify the result without
     * affecting any other object that also asked for the default working week.</p>
     *
     * @return a modifiable set of the five business days
     */
    public static EnumSet<Day> weekdays() {
        return EnumSet.range(MONDAY, FRIDAY);
    }

    /**
     * Formats a set of days as a readable, comma separated list.
     *
     * @param days the days to format; must not be {@code null}
     * @return for example {@code "Monday, Wednesday, Friday"}
     */
    public static String format(Set<Day> days) {
        return days.stream()
                .map(Day::getDisplayName)
                .collect(Collectors.joining(", "));
    }
}
