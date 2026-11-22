package library.exception;

/**
 * Custom checked exception used for all validation and business logic errors
 * in the Library Management System.
 * This exception is thrown by service classes when validation fails,
 * duplicate data is detected, or business rules are violated.
 *
 * @author Usman
 */
public class LibraryException extends Exception {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new LibraryException with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public LibraryException(String message) {
        super(message);
    }

    /**
     * Constructs a new LibraryException with the specified detail message and cause.
     *
     * @param message the detail message explaining the error
     * @param cause   the cause of this exception
     */
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
