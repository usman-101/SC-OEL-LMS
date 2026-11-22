package library.model;

/**
 * Enum representing the role of a user in the system.
 * ADMIN users have full access to manage books, members, and loans.
 * STUDENT users can browse books, make reservations, and view their history.
 *
 * @author Usman
 */
public enum Role {
    /** Administrator/Librarian role with full access. */
    ADMIN("Admin"),
    /** Student role with limited access. */
    STUDENT("Student");

    /** Human-readable name shown in the GUI. */
    private final String displayName;

    /**
     * Constructs a Role with the given display name.
     *
     * @param displayName the human-readable name for this role
     */
    Role(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of this role.
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
     * Parses a Role from a string (case-insensitive).
     *
     * @param text the text to parse
     * @return the matching Role
     * @throws IllegalArgumentException if no match is found
     */
    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.displayName.equalsIgnoreCase(text) || role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + text);
    }
}
