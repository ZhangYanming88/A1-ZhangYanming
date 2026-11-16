import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Entry point for the PROG2004 Assessment 1 health service appointment system.
 *
 * <p>This class contains no business rules of its own. It builds a small, realistic set of
 * objects and then exercises them, printing the results, which is exactly what the brief asks
 * for: a scripted simulation standing in for real users of the system. Everything the
 * demonstration relies on - validation, the professional hierarchy, the booking rules - lives in
 * the domain classes, so the same behaviour would be available to a future user interface
 * without change.</p>
 *
 * <p>The demonstration is split into the two halves the brief describes:</p>
 * <ol>
 *   <li>Part 3: the health professional hierarchy, driven through a single collection.</li>
 *   <li>Parts 4-6: bookings, filtering, sorting, cancellation, and the error cases.</li>
 * </ol>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public class AssignmentOne {

    /** Student name and number, printed in the banner. */
    private static final String STUDENT = "Zhang Yanming (25259706)";

    /** Format used for the date in the banner. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ENGLISH);

    /**
     * Application entry point.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        printBanner();

        List<HealthProfessional> professionals = buildTeam();
        demonstrateProfessionals(professionals);
        demonstrateAppointmentSystem(professionals);

        System.out.println();
        System.out.println(ConsoleFormat.rule());
        System.out.println(" End of demonstration.");
        System.out.println(ConsoleFormat.rule());
    }

    /**
     * Builds the clinic's team: three general practitioners and two of every other type.
     *
     * <p>The collection is declared as {@code List<HealthProfessional>}. That single declaration
     * is what allows every type to be handled by one loop, and it is why adding a fifth type
     * later would not require a single change to the code below.</p>
     *
     * @return the professionals employed by the clinic
     */
    private static List<HealthProfessional> buildTeam() {
        List<HealthProfessional> team = new ArrayList<>();

        team.add(new GeneralPractitioner("1001", "Alice Chen",
                EnumSet.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY), "Room 3B"));
        team.add(new GeneralPractitioner("1002", "Bao Li", "Room 1A"));
        team.add(new GeneralPractitioner("1003", "Carla Diaz",
                EnumSet.of(Day.TUESDAY, Day.THURSDAY), "Room 2C"));

        team.add(new Specialist("2001", "Daniel Wu", "Cardiology"));
        team.add(new Specialist("2002", "Eva Nguyen",
                EnumSet.of(Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY), "Radiology"));

        team.add(new Nurse("3001", "Farid Haddad", "Ward 2 - General Medicine"));
        team.add(new Nurse("3002", "Grace Okafor",
                EnumSet.of(Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY), "Ward 5 - Cardiology"));

        team.add(new Dietitian("4001", "Hana Sato", "Diabetes care"));
        team.add(new Dietitian("4002", "Ivan Petrov",
                EnumSet.of(Day.MONDAY, Day.FRIDAY), "Post-surgical recovery"));

        return team;
    }

    /**
     * Part 3: creates and displays health professional objects through one collection.
     *
     * @param professionals the clinic's team
     */
    private static void demonstrateProfessionals(List<HealthProfessional> professionals) {
        ConsoleFormat.printHeading("PART 3 - HEALTH PROFESSIONALS");
        System.out.println(" The clinic employs " + professionals.size()
                + " professionals of four different types.");
        System.out.println(" They are stored in a single List<HealthProfessional>, so the loop");
        System.out.println(" below never asks what kind of professional it is holding. Each");
        System.out.println(" object prints its own details and states its own duties.");

        for (HealthProfessional professional : professionals) {
            System.out.println();
            System.out.println(ConsoleFormat.thinRule());
            System.out.println(professional);
            ConsoleFormat.printField("Duties", professional.performRole());
        }

        ConsoleFormat.printSubHeading("Behaviour that belongs to one type only");
        System.out.println(" The methods below are not part of the shared contract, so the");
        System.out.println(" caller has to cast. That is deliberate: a caller who only knows");
        System.out.println(" about HealthProfessional cannot prescribe, because not every");
        System.out.println(" professional is allowed to.");
        System.out.println();

        GeneralPractitioner generalPractitioner = (GeneralPractitioner) professionals.get(0);
        System.out.println(" " + generalPractitioner.prescribe("Amoxicillin",
                "500 mg three times daily"));

        Specialist cardiologist = (Specialist) professionals.get(3);
        System.out.println(" " + cardiologist.requestDiagnosticTest("echocardiogram"));

        Nurse nurse = (Nurse) professionals.get(5);
        System.out.println(" " + nurse.takeBloodSample("Tom Baker", "full blood count"));

        Dietitian dietitian = (Dietitian) professionals.get(7);
        System.out.println(" " + dietitian.createMealPlan("Tom Baker", 8400));
    }

    /**
     * Parts 4 to 6: models appointments, manages the collection, and demonstrates both the
     * normal and the failing paths.
     *
     * @param professionals the clinic's team
     */
    private static void demonstrateAppointmentSystem(List<HealthProfessional> professionals) {
        ConsoleFormat.printHeading("PARTS 4-6 - APPOINTMENTS FOR "
                + DATE_FORMAT.format(LocalDate.now()));

        AppointmentManager manager = new AppointmentManager();
        System.out.println(" Clinic schedule: " + manager.getSchedule().describe());
        System.out.println(" Appointments can only be booked for today, and a professional");
        System.out.println(" can never be booked into the same time slot twice.");

        Patient tom = new Patient("Tom Baker", "0412 345 678");
        Patient mei = new Patient("Mei Lin", "0422 334 455");
        Patient sam = new Patient("Sam Patel", "0433 556 677");

        HealthProfessional gp1001 = professionalWithId(professionals, "1001");
        HealthProfessional gp1002 = professionalWithId(professionals, "1002");
        HealthProfessional specialist2001 = professionalWithId(professionals, "2001");
        HealthProfessional specialist2002 = professionalWithId(professionals, "2002");
        HealthProfessional nurse3001 = professionalWithId(professionals, "3001");
        HealthProfessional dietitian4001 = professionalWithId(professionals, "4001");

        ConsoleFormat.printSubHeading("Adding appointments");
        System.out.println(" " + manager.addAppointment(tom, gp1001, LocalTime.of(9, 0)));
        System.out.println(" " + manager.addAppointment(mei, gp1001, LocalTime.of(9, 30)));
        System.out.println(" " + manager.addAppointment(sam, specialist2001, LocalTime.of(9, 0)));
        System.out.println(" " + manager.addAppointment(tom, nurse3001, LocalTime.of(10, 0)));
        System.out.println(" " + manager.addAppointment(sam, dietitian4001, LocalTime.of(11, 0)));
        System.out.println(" " + manager.addAppointment(mei, specialist2002, LocalTime.of(14, 30)));
        System.out.println(" " + manager.addAppointment(mei, gp1002, LocalTime.of(10, 0)));

        System.out.println();
        System.out.println(" The next two attempts must fail: one double books a professional,");
        System.out.println(" the other asks for a time that is not a slot.");
        System.out.println(" " + manager.addAppointment(mei, gp1001, LocalTime.of(9, 0)));
        System.out.println(" " + manager.addAppointment(sam, gp1002, LocalTime.of(9, 15)));

        printAppointments("All appointments, earliest first", manager.getAllAppointments());

        printAppointments("Filtered by professional ID 1001",
                manager.findByProfessional("1001"));

        printAppointments("Filtered by patient mobile number 0422 334 455",
                manager.findByPatientNumber("0422 334 455"));

        ConsoleFormat.printSubHeading("Sorting a filtered subset");
        System.out.println(" Two separate filtered results are combined in the order");
        System.out.println(" 11:00 (dietitian) then 09:00 (GP). Sorting the combined list");
        System.out.println(" puts them back into time order.");

        List<Appointment> combined = new ArrayList<>();
        combined.addAll(manager.findByProfessional("4001"));
        combined.addAll(manager.findByProfessional("1001"));
        System.out.println();
        System.out.println(" Unsorted input order: "
                + combined.get(0).getTime() + ", " + combined.get(1).getTime());
        printAppointments("Sorted subset", manager.sortByTime(combined));

        ConsoleFormat.printSubHeading("Cancelling appointments");
        System.out.println(" " + manager.cancelAppointment("1001", LocalTime.of(9, 30)));
        System.out.println(" " + manager.cancelAppointment("0422 334 455"));

        System.out.println();
        System.out.println(" The next two attempts must fail: the first appointment does");
        System.out.println(" not exist, the second phone number has no bookings.");
        System.out.println(" " + manager.cancelAppointment("9999", LocalTime.of(16, 0)));
        System.out.println(" " + manager.cancelAppointment("0400 000 000"));

        printAppointments("Remaining appointments after cancellation",
                manager.getAllAppointments());

        System.out.println();
        System.out.println(" " + manager.getAppointmentCount()
                + " appointments remain in the collection.");
    }

    /**
     * Prints a list of appointments under a sub-heading.
     *
     * @param title        the sub-heading text
     * @param appointments the appointments to print
     */
    private static void printAppointments(String title, List<Appointment> appointments) {
        ConsoleFormat.printSubHeading(title + " (" + appointments.size() + ")");
        if (appointments.isEmpty()) {
            System.out.println("  (no appointments)");
            return;
        }
        for (Appointment appointment : appointments) {
            System.out.println();
            System.out.println(ConsoleFormat.thinRule());
            System.out.println(appointment);
        }
    }

    /**
     * Finds a professional by identifier.
     *
     * @param professionals the clinic's team
     * @param id            the identifier to look for
     * @return the matching professional, as the base type
     * @throws IllegalArgumentException if no professional has that identifier
     */
    private static HealthProfessional professionalWithId(List<HealthProfessional> professionals,
                                                         String id) {
        for (HealthProfessional professional : professionals) {
            if (professional.getId().equals(id)) {
                return professional;
            }
        }
        throw new IllegalArgumentException("No health professional with ID " + id);
    }

    /**
     * Prints the title block of the demonstration.
     */
    private static void printBanner() {
        System.out.println(ConsoleFormat.rule());
        System.out.println(" PROG2004 Assessment 1 - Health Service Appointment System");
        System.out.println(" " + STUDENT);
        System.out.println(" " + DATE_FORMAT.format(LocalDate.now()));
        System.out.println(ConsoleFormat.rule());
    }
}
