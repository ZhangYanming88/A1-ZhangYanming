import java.util.EnumSet;

/**
 * A dietitian: a health professional who assesses a patient's nutritional needs and designs the
 * dietary side of their treatment.
 *
 * <p>Adds the area of dietetics the professional focuses on, and the ability to produce a meal
 * plan. This is the fourth concrete type and the one that shows the hierarchy is not simply
 * "doctors of different seniority": a dietitian shares identity and roster with a surgeon but
 * shares almost no behaviour.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    /** The area of dietetics this professional focuses on, for example {@code "Diabetes care"}. */
    private final String dietaryFocus;

    /**
     * Creates a dietitian with an explicit weekly roster.
     *
     * @param id            the identifier, digits only
     * @param name          the dietitian's name
     * @param availableDays the days worked each week
     * @param dietaryFocus  the area of dietetics the professional focuses on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, EnumSet<Day> availableDays, String dietaryFocus) {
        super(id, name, availableDays);
        this.dietaryFocus = requireText(dietaryFocus, "The dietary focus");
    }

    /**
     * Creates a dietitian who works the standard Monday to Friday week.
     *
     * @param id           the identifier, digits only
     * @param name         the dietitian's name
     * @param dietaryFocus the area of dietetics the professional focuses on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, String dietaryFocus) {
        this(id, name, Day.weekdays(), dietaryFocus);
    }

    /**
     * Returns the area of dietetics this professional focuses on.
     *
     * @return the dietary focus
     */
    public String getDietaryFocus() {
        return dietaryFocus;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code "Dietitian"}
     */
    @Override
    public String getRoleTitle() {
        return "Dietitian";
    }

    /**
     * {@inheritDoc}
     *
     * @return a description of dietetic duties
     */
    @Override
    public String performRole() {
        return "Assesses nutritional needs and designs dietary plans, with a focus on "
                + dietaryFocus + ".";
    }

    /**
     * Produces a meal plan for a patient.
     *
     * <p>The daily energy target is validated rather than trusted, because a plan built on a
     * negative or zero target would be meaningless.</p>
     *
     * @param patientName    the patient the plan is for
     * @param dailyKilojoules the target daily energy intake in kilojoules, must be positive
     * @return a confirmation message
     * @throws IllegalArgumentException if the name is blank or the target is not positive
     */
    public String createMealPlan(String patientName, int dailyKilojoules) {
        String patient = requireText(patientName, "The patient name");
        if (dailyKilojoules <= 0) {
            throw new IllegalArgumentException(
                    "The daily energy target must be greater than zero, but was: "
                            + dailyKilojoules);
        }
        return String.format("%s prepared a daily meal plan of %d kJ for %s.",
                getName(), dailyKilojoules, patient);
    }

    /**
     * Returns the shared details followed by the dietitian specific ones.
     *
     * @return the formatted details
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(System.lineSeparator());
        appendField(builder, "Dietary focus", dietaryFocus);
        return builder.toString().stripTrailing();
    }
}
