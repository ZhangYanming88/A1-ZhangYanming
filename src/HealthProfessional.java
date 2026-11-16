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
 * <p>The class is {@code abstract}: the service only ever employs professionals who have a
 * specialty, so a "plain" health professional must not be creatable. Declaring the behaviour
 * abstract also forces every subclass to state what it does, which keeps the hierarchy honest
 * as it grows.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 2.0
 */
public abstract class HealthProfessional {

    /** An identifier must consist of one or more digits and nothing else. */
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

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
     * Returns the kind of health professional this object represents.
     *
     * <p>Abstract because only the subclass knows what it is. It is used by {@link #toString()}
     * so that every subtype identifies itself in exactly the same place in the output.</p>
     *
     * @return a short role label, for example {@code "General Practitioner"}
     */
    public abstract String getRoleTitle();

    /**
     * Describes the duties this professional performs for the health service.
     *
     * <p>This is the polymorphic operation of the hierarchy: the manager, the printing loop and
     * any future report only ever talk to {@code HealthProfessional}, yet each object answers
     * with its own behaviour.</p>
     *
     * @return a one sentence description of the role
     */
    public abstract String performRole();

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
        ConsoleFormat.appendField(builder, label, value);
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
        appendField(builder, "Role", getRoleTitle());
        appendField(builder, "Available", Day.format(availableDays));
        return builder.toString().stripTrailing();
    }

    /**
     * Compares this professional with another object.
     *
     * <p>Two professionals are the same person when they carry the same identifier. Identifiers
     * are issued by the health service and are unique across every role, so the comparison does
     * not need to look at the class or the name. This is the definition Part 5 relies on when it
     * checks whether a professional is already booked in a time slot.</p>
     *
     * @param other the object to compare against
     * @return {@code true} if both objects describe the same professional
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the identifier
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Validates free text used by this class and its subclasses.
     *
     * <p>Kept protected so subclasses can validate their own fields with the same message style
     * instead of repeating the null and blank checks.</p>
     *
     * @param value     the candidate text
     * @param fieldName the name of the field, used in the error message
     * @return the same text, trimmed
     * @throws IllegalArgumentException if the text is {@code null} or blank
     */
    protected static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }
        return value.trim();
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
        return requireText(name, "A health professional name");
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
