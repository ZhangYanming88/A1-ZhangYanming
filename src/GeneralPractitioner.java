import java.util.EnumSet;

/**
 * A general practitioner: the first point of contact for most patients.
 *
 * <p>Extends {@link HealthProfessional} to reuse the shared identity and roster, and adds the
 * one thing that is true of a GP but not of every professional: the consulting room they work
 * from, and the ability to prescribe medication.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional {

    /** Room in which this GP sees patients. */
    private final String consultingRoom;

    /**
     * Creates a general practitioner with an explicit weekly roster.
     *
     * @param id            the identifier, digits only
     * @param name          the GP's name
     * @param availableDays the days worked each week
     * @param consultingRoom the room the GP consults from
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, EnumSet<Day> availableDays,
                               String consultingRoom) {
        super(id, name, availableDays);
        this.consultingRoom = requireText(consultingRoom, "The consulting room");
    }

    /**
     * Creates a general practitioner who works the standard Monday to Friday week.
     *
     * @param id            the identifier, digits only
     * @param name          the GP's name
     * @param consultingRoom the room the GP consults from
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, String consultingRoom) {
        this(id, name, Day.weekdays(), consultingRoom);
    }

    /**
     * Returns the room this GP consults from.
     *
     * @return the consulting room
     */
    public String getConsultingRoom() {
        return consultingRoom;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code "General Practitioner"}
     */
    @Override
    public String getRoleTitle() {
        return "General Practitioner";
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of general practice duties
     */
    @Override
    public String performRole() {
        return "Screens, diagnoses and treats everyday illness, and refers patients "
                + "to specialists when more advanced care is needed.";
    }

    /**
     * Issues a prescription to the GP's current patient.
     *
     * <p>A behaviour that exists only on this branch of the hierarchy: a dietitian cannot
     * prescribe, so the method deliberately does not live on {@code HealthProfessional}.</p>
     *
     * @param medication the drug being prescribed
     * @param dosage     the dosage instructions
     * @return a confirmation message
     * @throws IllegalArgumentException if either argument is blank
     */
    public String prescribe(String medication, String dosage) {
        String drug = requireText(medication, "The medication");
        String dose = requireText(dosage, "The dosage");
        return String.format("%s prescribed %s (%s).", getName(), drug, dose);
    }

    /**
     * Returns the shared details followed by the GP specific ones.
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(System.lineSeparator());
        appendField(builder, "Consulting room", consultingRoom);
        return builder.toString().stripTrailing();
    }
}
