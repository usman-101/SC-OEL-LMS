package library.model;

/**
 * Represents a book in the library system.
 * Each book has a unique ID, title, author, category, status,
 * and optionally tracks who it is issued to and its due date.
 *
 * @author Usman
 */
public class Book {

    private String id;
    private String title;
    private String author;
    private Category category;
    private BookStatus status;
    private String issuedTo;
    private String dueDate;
    private String reservedBy;

    /**
     * Constructs a new Book with the specified details.
     * Status defaults to AVAILABLE.
     *
     * @param id       the unique book ID
     * @param title    the title of the book
     * @param author   the author of the book
     * @param category the category of the book
     */
    public Book(String id, String title, String author, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = BookStatus.AVAILABLE;
        this.issuedTo = "";
        this.dueDate = "";
        this.reservedBy = "";
    }

    /**
     * Constructs a Book with all fields specified (used for loading from storage).
     *
     * @param id         the unique book ID
     * @param title      the title of the book
     * @param author     the author of the book
     * @param category   the category of the book
     * @param status     the current status
     * @param issuedTo   the member ID the book is issued to
     * @param dueDate    the due date string
     * @param reservedBy the member ID who reserved the book
     */
    public Book(String id, String title, String author, Category category,
                BookStatus status, String issuedTo, String dueDate, String reservedBy) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.issuedTo = issuedTo != null ? issuedTo : "";
        this.dueDate = dueDate != null ? dueDate : "";
        this.reservedBy = reservedBy != null ? reservedBy : "";
    }

    /**
     * Returns the book ID.
     *
     * @return the book ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the book ID.
     *
     * @param id the book ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the book title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the book title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the book author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the book author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the book category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the book category.
     *
     * @param category the category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Returns the book status.
     *
     * @return the status
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * Sets the book status.
     *
     * @param status the status
     */
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    /**
     * Returns the member ID the book is issued to.
     *
     * @return the issued-to member ID, or empty string if not issued
     */
    public String getIssuedTo() {
        return issuedTo;
    }

    /**
     * Sets the member ID the book is issued to.
     *
     * @param issuedTo the member ID
     */
    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo != null ? issuedTo : "";
    }

    /**
     * Returns the due date as a string.
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
        this.dueDate = dueDate != null ? dueDate : "";
    }

    /**
     * Returns the member ID who reserved the book.
     *
     * @return the reserved-by member ID, or empty string if not reserved
     */
    public String getReservedBy() {
        return reservedBy;
    }

    /**
     * Sets the member ID who reserved the book.
     *
     * @param reservedBy the member ID
     */
    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy != null ? reservedBy : "";
    }

    /**
     * Returns a string representation of this book.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Book{id='" + id + "', title='" + title + "', author='" + author +
               "', category=" + category + ", status=" + status + "}";
    }
}
