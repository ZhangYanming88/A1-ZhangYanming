import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds every appointment for the day and provides the operations the health service asked for:
 * adding, viewing, filtering, sorting and cancelling.
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li><strong>Why an {@code ArrayList}.</strong> The clinic needs to keep appointments in the
 *       order they were booked, iterate over all of them repeatedly, and sort arbitrary subsets.
 *       Those are index based, order preserving operations, which is exactly what
 *       {@code ArrayList} is for. A {@code HashSet} would lose the ordering and would not allow
 *       two appointments at the same time for different professionals; a {@code HashMap} keyed by
 *       professional would make "show me the whole day" awkward. The collection is small (16
 *       slots per professional), so the linear scans used below are not a performance concern.</li>
 *   <li><strong>Feedback as return values.</strong> Every operation returns a message instead of
 *       printing one. The manager does not decide how the clinic talks to its staff; the
 *       demonstration decides that. This also makes the operations straightforward to test.</li>
 *   <li><strong>One place for the rules.</strong> The clinic's schedule and the rule that a
 *       professional cannot be double booked both live here, because both need a view of the
 *       whole day rather than a single appointment.</li>
 * </ul>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class AppointmentManager {

    /** Prefix used for a message reporting that an operation was refused. */
    public static final String WARNING_PREFIX = "WARNING: ";

    /** Prefix used for a message reporting that an operation succeeded. */
    public static final String SUCCESS_PREFIX = "OK: ";

    /** Format used when a time is shown to a reader. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Every appointment held by the manager, in booking order. */
    private final List<Appointment> appointments;

    /** The clinic's bookable slots. */
    private final ClinicSchedule schedule;

    /**
     * Creates a manager for a clinic that runs the standard 08:00-16:00 schedule.
     */
    public AppointmentManager() {
        this(ClinicSchedule.standard());
    }

    /**
     * Creates a manager for a clinic with a given schedule.
     *
     * <p>Accepting the schedule as a constructor argument is what allows the clinic to change
     * its opening hours without touching this class or any of its operations.</p>
     *
     * @param schedule the clinic's bookable slots, required
     * @throws IllegalArgumentException if the schedule is {@code null}
     */
    public AppointmentManager(ClinicSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("An appointment manager needs a clinic schedule.");
        }
        this.schedule = schedule;
        this.appointments = new ArrayList<>();
    }

    /**
     * Books an appointment.
     *
     * @param patient      the patient being seen
     * @param professional the professional who will see them
     * @param time         the start time today
     * @return a message describing the outcome; it begins with {@value #WARNING_PREFIX} when the
     *         booking was refused
     */
    public String addAppointment(Patient patient, HealthProfessional professional,
                                 LocalTime time) {
        if (patient == null) {
            return WARNING_PREFIX + "An appointment needs a patient. Nothing was booked.";
        }
        if (professional == null) {
            return WARNING_PREFIX + "An appointment needs a health professional. "
                    + "Nothing was booked.";
        }
        if (time == null) {
            return WARNING_PREFIX + "An appointment needs a start time. Nothing was booked.";
        }
        if (!schedule.isBookableSlot(time)) {
            return WARNING_PREFIX + TIME_FORMAT.format(time) + " is not a bookable time slot. "
                    + "The clinic runs " + schedule.describe() + ". Nothing was booked.";
        }

        Appointment candidate = new Appointment(patient, professional, time);
        for (Appointment existing : appointments) {
            if (existing.isSameSlotAs(candidate)) {
                return WARNING_PREFIX + describe(professional) + " is already booked at "
                        + TIME_FORMAT.format(time) + ". Nothing was booked.";
            }
        }

        appointments.add(candidate);
        return SUCCESS_PREFIX + "Booked " + TIME_FORMAT.format(time) + " for "
                + patient.getName() + " with " + describe(professional) + ".";
    }

    /**
     * Cancels the appointment a professional holds at a given time.
     *
     * @param professionalId the identifier of the professional
     * @param time           the start time of the appointment to cancel
     * @return a message describing the outcome
     */
    public String cancelAppointment(String professionalId, LocalTime time) {
        if (professionalId == null || professionalId.isBlank()) {
            return WARNING_PREFIX + "A professional ID is required to cancel by time.";
        }
        if (time == null) {
            return WARNING_PREFIX + "A time is required to cancel by professional.";
        }

        String wantedId = professionalId.trim();
        for (int index = 0; index < appointments.size(); index++) {
            Appointment candidate = appointments.get(index);
            if (candidate.getProfessional().getId().equals(wantedId)
                    && candidate.getTime().equals(time)) {
                appointments.remove(index);
                return SUCCESS_PREFIX + "Cancelled the " + TIME_FORMAT.format(time)
                        + " appointment with " + describe(candidate.getProfessional())
                        + " booked for " + candidate.getPatient().getName() + ".";
            }
        }
        return WARNING_PREFIX + "No appointment found for professional " + wantedId + " at "
                + TIME_FORMAT.format(time) + ". Nothing was cancelled.";
    }

    /**
     * Cancels every appointment held by the patient with a given mobile number.
     *
     * <p>Cancelling by phone number is ambiguous when a patient holds more than one appointment,
     * so rather than silently picking one, every appointment for that patient is cancelled and
     * the number affected is reported. A patient with one booking - the usual case - sees exactly
     * the expected result.</p>
     *
     * @param mobileNumber the patient's mobile number, formatted or unformatted
     * @return a message describing the outcome
     */
    public String cancelAppointment(String mobileNumber) {
        String digits;
        try {
            digits = Patient.normaliseMobile(mobileNumber);
        } catch (IllegalArgumentException invalid) {
            return WARNING_PREFIX + invalid.getMessage() + " Nothing was cancelled.";
        }

        List<Appointment> removed = new ArrayList<>();
        for (Appointment candidate : appointments) {
            if (candidate.getPatient().getMobileNumber().equals(digits)) {
                removed.add(candidate);
            }
        }
        if (removed.isEmpty()) {
            return WARNING_PREFIX + "No appointment found for mobile number "
                    + Patient.formatMobile(digits) + ". Nothing was cancelled.";
        }
        appointments.removeAll(removed);
        return SUCCESS_PREFIX + "Cancelled " + removed.size() + " appointment(s) for "
                + removed.get(0).getPatient().getName() + " ("
                + Patient.formatMobile(digits) + ").";
    }

    /**
     * Returns every appointment, earliest first.
     *
     * @return a sorted copy of the day's appointments
     */
    public List<Appointment> getAllAppointments() {
        return sortByTime(appointments);
    }

    /**
     * Returns the appointments of one professional, earliest first.
     *
     * @param professionalId the identifier of the professional
     * @return a sorted list, empty when the professional has no appointments
     */
    public List<Appointment> findByProfessional(String professionalId) {
        String wantedId = (professionalId == null) ? "" : professionalId.trim();
        List<Appointment> matches = new ArrayList<>();
        for (Appointment candidate : appointments) {
            if (candidate.getProfessional().getId().equals(wantedId)) {
                matches.add(candidate);
            }
        }
        return sortByTime(matches);
    }

    /**
     * Returns the appointments of one patient, earliest first.
     *
     * @param mobileNumber the patient's mobile number, formatted or unformatted
     * @return a sorted list, empty when the number has no appointments
     */
    public List<Appointment> findByPatientNumber(String mobileNumber) {
        List<Appointment> matches = new ArrayList<>();
        String digits;
        try {
            digits = Patient.normaliseMobile(mobileNumber);
        } catch (IllegalArgumentException invalid) {
            return matches;
        }
        for (Appointment candidate : appointments) {
            if (candidate.getPatient().getMobileNumber().equals(digits)) {
                matches.add(candidate);
            }
        }
        return sortByTime(matches);
    }

    /**
     * Sorts any collection of appointments into time order.
     *
     * <p>Returns a new list rather than sorting the argument, so a filtered subset can be sorted
     * for display without disturbing the order of the manager's own collection, and callers can
     * pass in an unmodifiable list without getting an exception.</p>
     *
     * @param source the appointments to sort
     * @return a new list in ascending time order
     */
    public List<Appointment> sortByTime(List<Appointment> source) {
        List<Appointment> copy = new ArrayList<>(source);
        Collections.sort(copy);
        return copy;
    }

    /**
     * Returns the schedule this manager books against.
     *
     * @return the clinic schedule
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Returns the number of appointments currently held.
     *
     * @return the appointment count
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    /**
     * Formats a professional for use inside a message.
     *
     * @param professional the professional to describe
     * @return for example {@code "1001 - Alice Chen (General Practitioner)"}
     */
    private static String describe(HealthProfessional professional) {
        return professional.getId() + " - " + professional.getName()
                + " - " + professional.getRoleTitle();
    }
}
