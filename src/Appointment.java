import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A single appointment: one patient, seeing one health professional, at one time on the current
 * day.
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li><strong>Composition.</strong> An appointment is not a kind of patient or a kind of
 *       professional; it <em>has</em> both. Holding references rather than copying their details
 *       means an appointment always shows the current name of a professional who has been
 *       renamed, and it can refer to any of the four professional types without knowing which
 *       one it holds - the field type is the base class.</li>
 *   <li><strong>Time.</strong> The time is a {@link LocalTime} rather than a {@code String} or a
 *       {@code Date}. {@code LocalTime} is already {@link Comparable}, so ordering a day's
 *       appointments is correct by construction instead of depending on text that happens to
 *       sort well until the clinic switches to a 12 hour clock.</li>
 *   <li><strong>Validity.</strong> Every component is required, so an appointment can never
 *       exist in an incomplete state. Whether the time is one of the clinic's slots, and whether
 *       the professional is already busy, are decisions that need the clinic's schedule and the
 *       full collection of appointments, so they are enforced by {@link AppointmentManager}.</li>
 * </ul>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    /** Format used when the time is shown to a reader. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** The patient being seen. */
    private final Patient patient;

    /** The professional who will see the patient. */
    private final HealthProfessional professional;

    /** Start time of the appointment today. */
    private final LocalTime time;

    /**
     * Creates an appointment.
     *
     * @param patient      the patient being seen, required
     * @param professional the professional who will see them, required
     * @param time         the start time on the current day, required, whole minutes only
     * @throws IllegalArgumentException if any argument is missing or the time carries seconds
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment must have a patient.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment must have a health professional.");
        }
        if (time == null) {
            throw new IllegalArgumentException("An appointment must have a start time.");
        }
        if (time.getSecond() != 0 || time.getNano() != 0) {
            throw new IllegalArgumentException(
                    "An appointment time must be a whole minute, but was: " + time);
        }
        this.patient = patient;
        this.professional = professional;
        this.time = time;
    }

    /**
     * Returns the patient being seen.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the professional who will see the patient.
     *
     * @return the professional, as the base type
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Returns the start time of the appointment.
     *
     * @return the start time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Reports whether another appointment would occupy the same professional at the same time.
     *
     * <p>This is the question Part 5 asks when it checks for a double booking. The comparison of
     * the professional is delegated to {@link HealthProfessional#equals(Object)}, which compares
     * identifiers, so a professional loaded twice from different places is still recognised as
     * the same person.</p>
     *
     * @param other the appointment to compare with; {@code null} returns {@code false}
     * @return {@code true} if both appointments would book the same professional at the same time
     */
    public boolean isSameSlotAs(Appointment other) {
        return other != null
                && time.equals(other.time)
                && professional.equals(other.professional);
    }

    /**
     * Orders appointments by time, earliest first.
     *
     * <p>Two appointments at the same time are ordered by professional identifier. The brief
     * allows either order there, but a stable tie-break means the output is identical on every
     * run, which makes the demonstration easy to follow.</p>
     *
     * @param other the appointment to compare with
     * @return a negative number, zero or a positive number as this appointment is earlier than,
     *         the same as, or later than the other one
     */
    @Override
    public int compareTo(Appointment other) {
        int byTime = time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        return professional.getId().compareTo(other.professional.getId());
    }

    /**
     * Returns a readable, multi-line description of this appointment.
     *
     * <p>The professional and patient appear through their own {@code toString} methods, so the
     * appointment does not need to know the details of either.</p>
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ConsoleFormat.appendField(builder, "Time", TIME_FORMAT.format(time));
        ConsoleFormat.appendField(builder, "Patient", patient);
        ConsoleFormat.appendField(builder, "Professional",
                professional.getId() + " - " + professional.getName()
                        + " - " + professional.getRoleTitle());
        return builder.toString().stripTrailing();
    }
}
