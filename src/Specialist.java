import java.util.EnumSet;

/**
 * A specialist: a doctor who has completed advanced training in one field of medicine, such as
 * cardiology, radiology or paediatrics.
 *
 * <p>The specialist records the field of medicine in addition to the shared professional
 * details, and can prescribe within that field as well as order diagnostic tests. The field is
 * stored as free text because the health service keeps adding new specialties and an
 * enumeration would need editing every time.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class Specialist extends HealthProfessional {

    /** The field of medicine this specialist practises, for example {@code "Cardiology"}. */
    private final String specialty;

    /**
     * Creates a specialist with an explicit weekly roster.
     *
     * @param id            the identifier, digits only
     * @param name          the specialist's name
     * @param availableDays the days worked each week
     * @param specialty     the field of medicine practised
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, EnumSet<Day> availableDays, String specialty) {
        super(id, name, availableDays);
        this.specialty = requireText(specialty, "The specialty");
    }

    /**
     * Creates a specialist who works the standard Monday to Friday week.
     *
     * @param id        the identifier, digits only
     * @param name      the specialist's name
     * @param specialty the field of medicine practised
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Specialist(String id, String name, String specialty) {
        this(id, name, Day.weekdays(), specialty);
    }

    /**
     * Returns the field of medicine this specialist practises.
     *
     * @return the specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The field of medicine is included in the title so that a printed list of professionals
     * tells the reader immediately which kind of specialist each entry is.</p>
     *
     * @return for example {@code "Specialist (Cardiology)"}
     */
    @Override
    public String getRoleTitle() {
        return "Specialist (" + specialty + ")";
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of specialist duties
     */
    @Override
    public String performRole() {
        return "Provides advanced assessment, diagnosis and treatment within " + specialty + ".";
    }

    /**
     * Issues a prescription within this specialist's field.
     *
     * @param medication the drug being prescribed
     * @param dosage     the dosage instructions
     * @return a confirmation message
     * @throws IllegalArgumentException if either argument is blank
     */
    public String prescribe(String medication, String dosage) {
        String drug = requireText(medication, "The medication");
        String dose = requireText(dosage, "The dosage");
        return String.format("%s (%s) prescribed %s (%s).", getName(), specialty, drug, dose);
    }

    /**
     * Orders a diagnostic test for a patient.
     *
     * @param testName the test required, for example {@code "Echocardiogram"}
     * @return a confirmation message
     * @throws IllegalArgumentException if the test name is blank
     */
    public String requestDiagnosticTest(String testName) {
        String test = requireText(testName, "The test name");
        return String.format("%s ordered a diagnostic test (%s) for the next available appointment.",
                getName(), test);
    }

    /**
     * Returns the shared details followed by the specialist specific ones.
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(System.lineSeparator());
        appendField(builder, "Specialty", specialty);
        return builder.toString().stripTrailing();
    }
}
