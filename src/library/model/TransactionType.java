package library.model;

/**
 * Enum representing the type of a transaction in the library system.
 * Tracks all operations performed on books and memberships.
 *
 * @author Usman
 */
public enum TransactionType {
    /** A book was added to the library. */
    ADD("Add"),
    /** A book's details were updated. */
    UPDATE("Update"),
    /** A book was deleted from the library. */
    DELETE("Delete"),
    /** A book's status was changed. */
    STATUS_CHANGE("Status Change"),
    /** A book was issued to a member. */
    ISSUE("Issue"),
    /** A book was returned by a member. */
    RETURN("Return"),
    /** A book was reserved by a member. */
    RESERVE("Reserve"),
    /** A reservation was cancelled. */
    CANCEL_RESERVATION("Cancel Reservation");

    private final String displayName;

    /**
     * Constructs a TransactionType with the given display name.
     *
     * @param displayName the human-readable name for this transaction type
     */
    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of this transaction type.
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
     * Parses a TransactionType from a string (case-insensitive).
     *
     * @param text the text to parse
     * @return the matching TransactionType
     * @throws IllegalArgumentException if no match is found
     */
    public static TransactionType fromString(String text) {
        for (TransactionType type : TransactionType.values()) {
            if (type.displayName.equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + text);
    }
}
