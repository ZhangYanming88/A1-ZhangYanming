import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The fixed daily schedule of bookable time slots.
 *
 * <p>Part 5 asks how to represent a fixed set of slots so that the clinic can change its hours
 * without the rest of the system needing to change. The answer is to make the schedule an
 * object rather than a scattering of hard coded times: it is built from an opening time, a
 * closing time and a slot length, all of which can be supplied to the constructor. A clinic
 * that opens at 07:30 in fifteen minute slots is then a different {@code ClinicSchedule}
 * instance, and no other class has to know about it - the manager simply asks the schedule
 * whether a given time is bookable.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public final class ClinicSchedule {

    /** Default opening time of the clinic. */
    public static final LocalTime DEFAULT_OPENING = LocalTime.of(8, 0);

    /** Default closing time of the clinic. */
    public static final LocalTime DEFAULT_CLOSING = LocalTime.of(16, 0);

    /** Default length of one appointment slot, in minutes. */
    public static final int DEFAULT_SLOT_MINUTES = 30;

    /** Format used when a time is shown to a reader. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** The bookable start times, in ascending order. */
    private final List<LocalTime> slots;

    /** When the clinic opens. */
    private final LocalTime openingTime;

    /** When the clinic closes. */
    private final LocalTime closingTime;

    /** Length of one slot in minutes. */
    private final int slotMinutes;

    /**
     * Creates a schedule and generates its slots.
     *
     * @param openingTime the first bookable start time
     * @param closingTime the time the clinic closes; the last slot starts before this
     * @param slotMinutes the length of one appointment slot in minutes
     * @throws IllegalArgumentException if the times are not in order, the slot length is not
     *                                  positive, or the opening hours do not divide evenly into
     *                                  slots
     */
    public ClinicSchedule(LocalTime openingTime, LocalTime closingTime, int slotMinutes) {
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times must be supplied.");
        }
        if (!closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException(
                    "The closing time must be after the opening time.");
        }
        if (slotMinutes <= 0) {
            throw new IllegalArgumentException(
                    "The slot length must be greater than zero, but was: " + slotMinutes);
        }
        long totalMinutes = java.time.Duration.between(openingTime, closingTime).toMinutes();
        if (totalMinutes % slotMinutes != 0) {
            throw new IllegalArgumentException(
                    "The opening hours must divide evenly into " + slotMinutes + " minute slots.");
        }

        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotMinutes = slotMinutes;

        List<LocalTime> generated = new ArrayList<>();
        for (LocalTime time = openingTime; time.isBefore(closingTime);
                time = time.plusMinutes(slotMinutes)) {
            generated.add(time);
        }
        this.slots = Collections.unmodifiableList(generated);
    }

    /**
     * Creates the standard schedule used by the clinic: 08:00 to 16:00 in thirty minute slots.
     *
     * @return the default schedule
     */
    public static ClinicSchedule standard() {
        return new ClinicSchedule(DEFAULT_OPENING, DEFAULT_CLOSING, DEFAULT_SLOT_MINUTES);
    }

    /**
     * Returns every bookable start time in ascending order.
     *
     * @return an unmodifiable list of slot start times
     */
    public List<LocalTime> getSlots() {
        return slots;
    }

    /**
     * Reports whether a time is a bookable start of a slot.
     *
     * @param time the time to test
     * @return {@code true} if an appointment may start at that time
     */
    public boolean isBookableSlot(LocalTime time) {
        return slots.contains(time);
    }

    /**
     * Returns the number of slots in the day.
     *
     * @return the slot count
     */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Formats the first and last bookable time for a given slot length.
     *
     * @return for example {@code "08:00-15:30"}
     */
    public String getBookableRange() {
        LocalTime lastStart = slots.get(slots.size() - 1);
        return TIME_FORMAT.format(openingTime) + "-" + TIME_FORMAT.format(lastStart);
    }

    /**
     * Describes the schedule in one sentence, for use in messages.
     *
     * @return for example {@code "08:00-16:00 in 30 minute slots (16 bookable times)"}
     */
    public String describe() {
        return TIME_FORMAT.format(openingTime) + "-" + TIME_FORMAT.format(closingTime)
                + " in " + slotMinutes + " minute slots (" + slots.size() + " bookable times)";
    }
}
