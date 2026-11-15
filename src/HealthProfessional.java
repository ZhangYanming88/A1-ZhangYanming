import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a health professional working for the health service.
 *
 * <p>This class captures everything that is common to <em>every</em> kind of health
 * professional, regardless of specialty: an identifier, a name and the fixed set of days the
 * professional works each week. The specialty specific information and behaviour live in the
 * subclasses created in Part 2.</p>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li><strong>Encapsulation.</strong> All state is private. External code can read it through
 *       accessors but can only change it through validated operations, so an object can never be
 *       put into an invalid state.</li>
 *   <li><strong>Identity.</strong> The identifier is stored as a {@code String} rather than an
 *       {@code int}: it is a label, not a number to calculate with, and leading zeros must be
 *       preserved. It is still validated to contain digits only, as the brief requires.</li>
 *   <li><strong>Defensive copying.</strong> The available days are held in an {@link EnumSet};
 *       the accessor hands back a copy so that no caller can silently change a professional's
 *       roster from the outside.</li>
 * </ul>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class HealthProfessional {

    /** An identifier must consist of one or more digits and nothing else. */
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    /** Width used to align the labels of the printed detail block. */
    private static final int LABEL_WIDTH = 15;

    /** Unique identifier of the professional; digits only. */
    private final String id;

    /** Full name of the professional. */
    private String name;

    /** The days of the week this professional works; never empty. */
    private final EnumSet<Day> availableDays;

    /**
     * Creates a health professional with an explicit weekly roster.
     *
     * @param id           the identifier, containing digits only
     * @param name         the professional's name, must not be blank
     * @param availableDays the days worked each week, must not be {@code null} or empty
     * @throws IllegalArgumentException if any argument is invalid
     * @throws NullPointerException     if {@code availableDays} is {@code null}
     */
    public HealthProfessional(String id, String name, EnumSet<Day> availableDays) {
        this.id = validateId(id);
        this.name = validateName(name);
        this.availableDays = copyDays(availableDays);
    }

    /**
     * Creates a health professional who works the standard Monday to Friday week.
     *
     * <p>This overloaded constructor gives callers a sensible default instead of forcing them to
     * describe a roster that is, for most professionals, the ordinary one.</p>
     *
     * @param id   the identifier, containing digits only
     * @param name the professional's name, must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public HealthProfessional(String id, String name) {
        this(id, name, Day.weekdays());
    }

    /**
     * Returns the identifier of this professional.
     *
     * @return the identifier, digits only
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this professional.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Renames this professional.
     *
     * @param name the new name, must not be blank
     * @throws IllegalArgumentException if the name is {@code null} or blank
     */
    public void setName(String name) {
        this.name = validateName(name);
    }

    /**
     * Returns a copy of the days this professional works.
     *
     * <p>The returned set is a defensive copy: changing it has no effect on this object.</p>
     *
     * @return the days worked each week
     */
    public EnumSet<Day> getAvailableDays() {
        return EnumSet.copyOf(availableDays);
    }

    /**
     * Reports whether this professional works on a given day.
     *
     * @param day the day to test
     * @return {@code true} if the professional is available on that day
     */
    public boolean isAvailableOn(Day day) {
        return availableDays.contains(day);
    }

    /**
     * Builds the printed representation of a single labelled field.
     *
     * <p>Provided as a protected helper so subclasses can append their own fields in exactly the
     * same layout instead of duplicating formatting code.</p>
     *
     * @param builder the buffer being built
     * @param label   the field label
     * @param value   the field value
     */
    protected static void appendField(StringBuilder builder, String label, Object value) {
        builder.append(String.format("  %-" + LABEL_WIDTH + "s: %s%n", label, value));
    }

    /**
     * Returns a readable, multi-line description of this professional.
     *
     * <p>Overriding {@code toString} means the details are shown automatically whenever the
     * object is printed, for example with {@code System.out.println(doctorA)}.</p>
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendField(builder, "ID", id);
        appendField(builder, "Name", name);
        appendField(builder, "Available", Day.format(availableDays));
        return builder.toString().stripTrailing();
    }

    /**
     * Validates and returns an identifier.
     *
     * @param id the candidate identifier
     * @return the same identifier, trimmed
     * @throws IllegalArgumentException if the identifier is {@code null}, blank or not digits only
     */
    private static String validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("A health professional ID must not be empty.");
        }
        String trimmed = id.trim();
        if (!ID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "A health professional ID must contain digits only, but was: " + id);
        }
        return trimmed;
    }

    /**
     * Validates and returns a name.
     *
     * @param name the candidate name
     * @return the same name, trimmed
     * @throws IllegalArgumentException if the name is {@code null} or blank
     */
    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A health professional name must not be empty.");
        }
        return name.trim();
    }

    /**
     * Validates a weekly roster and stores an independent copy of it.
     *
     * @param days the candidate roster
     * @return a defensive copy of the roster
     * @throws NullPointerException     if {@code days} is {@code null}
     * @throws IllegalArgumentException if the roster is empty
     */
    private static EnumSet<Day> copyDays(EnumSet<Day> days) {
        Objects.requireNonNull(days, "The set of available days must not be null.");
        if (days.isEmpty()) {
            throw new IllegalArgumentException("A health professional must work on at least one day.");
        }
        return EnumSet.copyOf(days);
    }
}
