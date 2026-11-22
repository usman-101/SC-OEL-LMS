package library.model;

/**
 * Enum representing the status of a book in the library.
 * Each status has a human-readable display name.
 *
 * @author Usman
 */
public enum BookStatus {
    /** Book is available for borrowing. */
    AVAILABLE("Available"),
    /** Book is currently issued to a member. */
    ISSUED("Issued"),
    /** Book is reserved by a member. */
    RESERVED("Reserved");

    /** Human-readable name shown in the GUI. */
    private final String displayName;

    /**
     * Constructs a BookStatus with the given display name.
     *
     * @param displayName the human-readable name for this status
     */
    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of this status.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the display name as the string representation.
     *
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Parses a BookStatus from a string (case-insensitive).
     *
     * @param text the text to parse
     * @return the matching BookStatus
     * @throws IllegalArgumentException if no match is found
     */
    public static BookStatus fromString(String text) {
        for (BookStatus status : BookStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown book status: " + text);
    }
}
