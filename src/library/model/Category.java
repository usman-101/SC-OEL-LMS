package library.model;

/**
 * Enum representing the category of a book in the library.
 * Categories include Programming, Science, Mathematics, Literature,
 * History, Technology, and Other.
 *
 * @author Usman
 */
public enum Category {
    /** Programming books. */
    PROGRAMMING("Programming"),
    /** Science books. */
    SCIENCE("Science"),
    /** Mathematics books. */
    MATHEMATICS("Mathematics"),
    /** Literature books. */
    LITERATURE("Literature"),
    /** History books. */
    HISTORY("History"),
    /** Technology books. */
    TECHNOLOGY("Technology"),
    /** Other category for miscellaneous books. */
    OTHER("Other");

    private final String displayName;

    /**
     * Constructs a Category with the given display name.
     *
     * @param displayName the human-readable name for this category
     */
    Category(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of this category.
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
     * Parses a Category from a string (case-insensitive).
     *
     * @param text the text to parse
     * @return the matching Category
     * @throws IllegalArgumentException if no match is found
     */
    public static Category fromString(String text) {
        for (Category cat : Category.values()) {
            if (cat.displayName.equalsIgnoreCase(text) || cat.name().equalsIgnoreCase(text)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + text);
    }
}
