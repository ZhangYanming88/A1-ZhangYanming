/**
 * Small helpers for producing readable console output.
 *
 * <p>The printed reports in this program all use the same label width and the same horizontal
 * rules. Keeping those in one place means a card printed for a health professional, an
 * appointment or a manager report lines up identically, and a change to the layout is made
 * once rather than in every class that prints something.</p>
 *
 * <p>This is a utility class: it holds no state and cannot be instantiated.</p>
 *
 * @author Zhang Yanming (25259706)
 * @version 1.0
 */
public final class ConsoleFormat {

    /** Column at which the colon of every label/value line is placed. */
    private static final int LABEL_WIDTH = 16;

    /** Number of characters in a full width horizontal rule. */
    private static final int RULE_WIDTH = 76;

    /** Prevents instantiation of this utility class. */
    private ConsoleFormat() {
    }

    /**
     * Appends one aligned label/value line to a buffer.
     *
     * @param builder the buffer being built
     * @param label   the field label
     * @param value   the field value; {@code null} prints as {@code "(none)"}
     */
    public static void appendField(StringBuilder builder, String label, Object value) {
        String text = (value == null) ? "(none)" : value.toString();
        builder.append(String.format("  %-" + LABEL_WIDTH + "s: %s%n", label, text));
    }

    /**
     * Returns a full width horizontal rule.
     *
     * @return a line of {@code '='} characters
     */
    public static String rule() {
        return "=".repeat(RULE_WIDTH);
    }

    /**
     * Returns a thin horizontal rule, used to separate records inside a section.
     *
     * @return a line of {@code '-'} characters
     */
    public static String thinRule() {
        return "-".repeat(RULE_WIDTH);
    }

    /**
     * Prints a section heading surrounded by rules.
     *
     * @param title the heading text
     */
    public static void printHeading(String title) {
        System.out.println();
        System.out.println(rule());
        System.out.println(" " + title);
        System.out.println(rule());
    }

    /**
     * Prints a smaller sub-heading, used inside a section.
     *
     * @param title the sub-heading text
     */
    public static void printSubHeading(String title) {
        System.out.println();
        System.out.println("-- " + title + " " + "-".repeat(Math.max(0, RULE_WIDTH - title.length() - 4)));
    }

    /**
     * Prints a label/value line directly to the console.
     *
     * @param label the field label
     * @param value the field value
     */
    public static void printField(String label, Object value) {
        StringBuilder builder = new StringBuilder();
        appendField(builder, label, value);
        System.out.print(builder);
    }
}
