package library.model;

/**
 * Represents a loan record for a book issued to a member.
 * Tracks issue date, due date, return date, and loan status.
 *
 * @author Usman
 */
public class Loan {

    private String loanId;
    private String bookId;
    private String memberId;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private boolean active;

    /**
     * Constructs a new Loan with the specified details.
     *
     * @param loanId    the unique loan ID
     * @param bookId    the ID of the book being loaned
     * @param memberId  the ID of the member borrowing the book
     * @param issueDate the date the book was issued (yyyy-MM-dd)
     * @param dueDate   the date the book is due (yyyy-MM-dd)
     */
    public Loan(String loanId, String bookId, String memberId, String issueDate, String dueDate) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = "";
        this.active = true;
    }

    /**
     * Constructs a Loan with all fields (used for loading from storage).
     *
     * @param loanId     the unique loan ID
     * @param bookId     the book ID
     * @param memberId   the member ID
     * @param issueDate  the issue date
     * @param dueDate    the due date
     * @param returnDate the return date (empty if not returned)
     * @param active     whether the loan is active
     */
    public Loan(String loanId, String bookId, String memberId, String issueDate,
                String dueDate, String returnDate, boolean active) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate != null ? returnDate : "";
        this.active = active;
    }

    /**
     * Returns the loan ID.
     *
     * @return the loan ID
     */
    public String getLoanId() {
        return loanId;
    }

    /**
     * Sets the loan ID.
     *
     * @param loanId the loan ID
     */
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    /**
     * Returns the book ID.
     *
     * @return the book ID
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
        this.bookId = bookId;
    }

    /**
     * Returns the member ID.
     *
     * @return the member ID
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
        this.memberId = memberId;
    }

    /**
     * Returns the issue date.
     *
     * @return the issue date string
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the issue date.
     *
     * @param issueDate the issue date string
     */
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Returns the due date.
     *
     * @return the due date string
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the due date string
     */
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Returns the return date.
     *
     * @return the return date string, or empty if not returned
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the return date string
     */
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate != null ? returnDate : "";
    }

    /**
     * Returns whether the loan is active.
     *
     * @return true if the loan is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the loan is active.
     *
     * @param active true if the loan is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns a string representation of this loan.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Loan{id='" + loanId + "', book='" + bookId + "', member='" + memberId +
               "', issued=" + issueDate + ", due=" + dueDate + ", active=" + active + "}";
    }
}
