import java.util.EnumSet;

/**
 * A nurse: a health professional who monitors patients, carries out clinical tasks and supports
 * the doctors on a ward.
 *
 * <p>Nurses do not prescribe, but they do take samples, which is the behaviour this class adds
 * to the shared professional contract.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class Nurse extends HealthProfessional {

    /** The ward this nurse is rostered on, for example {@code "Ward 2 - General Medicine"}. */
    private final String ward;

    /**
     * Creates a nurse with an explicit weekly roster.
     *
     * @param id            the identifier, digits only
     * @param name          the nurse's name
     * @param availableDays the days worked each week
     * @param ward          the ward the nurse is rostered on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, EnumSet<Day> availableDays, String ward) {
        super(id, name, availableDays);
        this.ward = requireText(ward, "The ward");
    }

    /**
     * Creates a nurse who works the standard Monday to Friday week.
     *
     * @param id   the identifier, digits only
     * @param name the nurse's name
     * @param ward the ward the nurse is rostered on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, String ward) {
        this(id, name, Day.weekdays(), ward);
    }

    /**
     * Returns the ward this nurse is rostered on.
     *
     * @return the ward
     */
    public String getWard() {
        return ward;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code "Nurse"}
     */
    @Override
    public String getRoleTitle() {
        return "Nurse";
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of nursing duties
     */
    @Override
    public String performRole() {
        return "Monitors patients, administers medication and assists the medical team on "
                + ward + ".";
    }

    /**
     * Records that this nurse has taken a blood sample from a patient.
     *
     * @param patientName the patient the sample was taken from
     * @param sampleType  the type of sample, for example {@code "full blood count"}
     * @return a confirmation message
     * @throws IllegalArgumentException if either argument is blank
     */
    public String takeBloodSample(String patientName, String sampleType) {
        String patient = requireText(patientName, "The patient name");
        String type = requireText(sampleType, "The sample type");
        return String.format("%s took a %s sample from %s.", getName(), type, patient);
    }

    /**
     * Returns the shared details followed by the nurse specific ones.
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(System.lineSeparator());
        appendField(builder, "Ward", ward);
        return builder.toString().stripTrailing();
    }
}
