package library.model;

/**
 * Represents a transaction record in the library system.
 * Each transaction logs an operation performed on a book or member,
 * including the timestamp, type, involved IDs, performer, and details.
 *
 * @author Usman
 */
public class Transaction {

    /** Timestamp (yyyy-MM-dd HH:mm:ss). */
    private String dateTime;
    /** Type of operation. */
    private TransactionType type;
    /** Book involved (may be empty). */
    private String bookId;
    /** Member involved (may be empty). */
    private String memberId;
    /** Email of the user who performed the operation. */
    private String performedBy;
    /** Human-readable details. */
    private String details;

    /**
     * Constructs a new Transaction with the specified details.
     *
     * @param dateTime    the date and time of the transaction (yyyy-MM-dd HH:mm:ss)
     * @param type        the type of transaction
     * @param bookId      the book ID involved (empty if not applicable)
     * @param memberId    the member ID involved (empty if not applicable)
     * @param performedBy the email of the user who performed the action
     * @param details     additional details about the transaction
     */
    public Transaction(String dateTime, TransactionType type, String bookId,
                       String memberId, String performedBy, String details) {
        this.dateTime = dateTime;
        this.type = type;
        this.bookId = bookId != null ? bookId : "";
        this.memberId = memberId != null ? memberId : "";
        this.performedBy = performedBy;
        this.details = details;
    }

    /**
     * Returns the date and time of the transaction.
     *
     * @return the dateTime string
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the transaction.
     *
     * @param dateTime the dateTime string
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the type of transaction.
     *
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the type of transaction.
     *
     * @param type the transaction type
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Returns the book ID involved in this transaction.
     *
     * @return the book ID, or empty string if not applicable
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets the book ID.
     *
     * @param bookId the book ID
     */
    public void setBookId(String bookId) {
        this.bookId = bookId != null ? bookId : "";
    }

    /**
     * Returns the member ID involved in this transaction.
     *
     * @return the member ID, or empty string if not applicable
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the member ID.
     *
     * @param memberId the member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId != null ? memberId : "";
    }

    /**
     * Returns the email of the user who performed this transaction.
     *
     * @return the performer's email
     */
    public String getPerformedBy() {
        return performedBy;
    }

    /**
     * Sets the performer's email.
     *
     * @param performedBy the performer's email
     */
    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    /**
     * Returns additional details about this transaction.
     *
     * @return the details string
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets additional details.
     *
     * @param details the details string
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Returns a string representation of this transaction.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Transaction{" + dateTime + ", " + type + ", book=" + bookId +
               ", member=" + memberId + ", by=" + performedBy + "}";
    }
}
