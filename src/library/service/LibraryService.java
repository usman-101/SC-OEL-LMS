package library.service;

import library.exception.LibraryException;
import library.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class providing all book-related business logic and library operations.
 * Handles adding, viewing, updating, deleting books, issuing, returning,
 * reservations, search, filter, overdue tracking, and statistics.
 * All validation is performed here, NOT in the GUI layer.
 *
 * @author Usman
 */
public class LibraryService {

    /** Search-by option: match on the book ID. */
    public static final String SEARCH_BY_ID = "Book ID";
    /** Search-by option: match on the title. */
    public static final String SEARCH_BY_TITLE = "Title";
    /** Search-by option: match on the author. */
    public static final String SEARCH_BY_AUTHOR = "Author";
    /** Filter value meaning "no filter". */
    public static final String ALL = "All";

    private final List<Book> books;
    private final List<Loan> loans;
    private int loanCounter;

    /**
     * Constructs a new LibraryService with empty book and loan lists.
     */
    public LibraryService() {
        this.books = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.loanCounter = 0;
    }

    /**
     * Constructs a LibraryService with existing books and loans (for loading from storage).
     *
     * @param books the list of books
     * @param loans the list of loans
     */
    public LibraryService(List<Book> books, List<Loan> loans) {
        this.books = books != null ? books : new ArrayList<>();
        this.loans = loans != null ? loans : new ArrayList<>();
        this.loanCounter = this.loans.size();
    }

    /**
     * Sets the books list (used when loading from storage).
     *
     * @param books the list of books to set
     */
    public void setBooks(List<Book> books) {
        this.books.clear();
        if (books != null) {
            this.books.addAll(books);
        }
    }

    /**
     * Sets the loans list (used when loading from storage).
     *
     * @param loans the list of loans to set
     */
    public void setLoans(List<Loan> loans) {
        this.loans.clear();
        if (loans != null) {
            this.loans.addAll(loans);
            this.loanCounter = this.loans.size();
        }
    }

    // ==================== ADD / VIEW ====================

    /**
     * Adds a new book to the library after validation.
     *
     * @param book the book to add
     * @throws LibraryException if validation fails (empty fields or duplicate ID)
     */
    public void addBook(Book book) throws LibraryException {
        validateBook(book);
        if (findById(book.getId()) != null) {
            throw new LibraryException("A book with ID '" + book.getId() + "' already exists.");
        }
        books.add(book);
    }

    /**
     * Returns all books in the library.
     *
     * @return an unmodifiable list of all books
     */
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Checks whether the library has no books.
     *
     * @return true if the library is empty
     */
    public boolean isEmpty() {
        return books.isEmpty();
    }

    /**
     * Finds a book by its ID.
     *
     * @param id the book ID to search for
     * @return the Book if found, or null if not found
     */
    public Book findById(String id) {
        if (id == null) return null;
        String trimmed = id.trim();
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(trimmed)) {
                return book;
            }
        }
        return null;
    }

    // ==================== UPDATE / DELETE / STATUS ====================

    /**
     * Updates the details of an existing book.
     *
     * @param id       the ID of the book to update
     * @param title    the new title
     * @param author   the new author
     * @param category the new category
     * @return the updated book
     * @throws LibraryException if the book is not found or new values are invalid
     */
    public Book updateBook(String id, String title, String author, Category category) throws LibraryException {
        Book book = requireBook(id);
        if (title == null || title.trim().isEmpty()) {
            throw new LibraryException("Book title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new LibraryException("Book author cannot be empty.");
        }
        if (category == null) {
            throw new LibraryException("Book category cannot be empty.");
        }
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setCategory(category);
        return book;
    }

    /**
     * Deletes a book from the library.
     * Cannot delete a book that is currently issued.
     *
     * @param id the ID of the book to delete
     * @return the deleted book
     * @throws LibraryException if the ID is empty, the book is not found or it is currently issued
     */
    public Book deleteBook(String id) throws LibraryException {
        Book book = requireBook(id);
        if (book.getStatus() == BookStatus.ISSUED || hasActiveLoan(book.getId())) {
            throw new LibraryException("Cannot delete book '" + book.getTitle()
                    + "' because it is currently issued. Return it first.");
        }
        books.remove(book);
        return book;
    }

    /**
     * Manually sets the status of a book (librarian action).
     * Rules: the new status must differ from the current one, and a book with an
     * active loan cannot be changed manually (it must be returned first).
     * Switching to AVAILABLE clears issue/reservation details.
     *
     * @param id     the book ID
     * @param status the new status
     * @return the updated book
     * @throws LibraryException if the ID is empty, the book is not found, the status is null,
     *                          unchanged, or the book has an active loan
     */
    public Book setStatus(String id, BookStatus status) throws LibraryException {
        Book book = requireBook(id);
        if (status == null) {
            throw new LibraryException("Please choose a status.");
        }
        if (book.getStatus() == status) {
            throw new LibraryException("Book '" + book.getTitle() + "' is already " + status.getDisplayName() + ".");
        }
        if (hasActiveLoan(book.getId())) {
            throw new LibraryException("Book '" + book.getTitle()
                    + "' has an active loan. Use Return on the Issue/Return screen instead.");
        }
        book.setStatus(status);
        book.setIssuedTo("");
        book.setDueDate("");
        book.setReservedBy("");
        return book;
    }

    /**
     * Finds a book by ID or throws a descriptive exception.
     *
     * @param id the book ID
     * @return the matching book (never null)
     * @throws LibraryException if the ID is empty or no such book exists
     */
    public Book requireBook(String id) throws LibraryException {
        if (id == null || id.trim().isEmpty()) {
            throw new LibraryException("Book ID cannot be empty.");
        }
        Book book = findById(id);
        if (book == null) {
            throw new LibraryException("Book with ID '" + id.trim() + "' not found.");
        }
        return book;
    }

    /**
     * Checks whether a book currently has an active (not returned) loan.
     *
     * @param bookId the book ID
     * @return true if an active loan exists for the book
     */
    public boolean hasActiveLoan(String bookId) {
        return findActiveLoanForBook(bookId) != null;
    }

    /**
     * Finds the active loan for a book, if any.
     *
     * @param bookId the book ID
     * @return the active loan, or null if the book is not on loan
     */
    public Loan findActiveLoanForBook(String bookId) {
        if (bookId == null) return null;
        for (Loan loan : loans) {
            if (loan.isActive() && loan.getBookId().equalsIgnoreCase(bookId.trim())) {
                return loan;
            }
        }
        return null;
    }

    // ==================== SEARCH / FILTER ====================

    /**
     * Searches books by a given field with case-insensitive partial matching.
     *
     * @param query   the search query
     * @param searchBy the field to search: "Book ID", "Title", or "Author"
     * @return list of matching books
     */
    public List<Book> search(String query, String searchBy) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(books);
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        String field = searchBy == null ? "" : searchBy.trim();
        return books.stream().filter(b -> matches(b, q, field)).collect(Collectors.toList());
    }

    /**
     * Checks whether a book matches a lower-case query on the given field.
     *
     * @param b     the book
     * @param q     the lower-case query
     * @param field "Book ID", "Title", "Author" or anything else for all fields
     * @return true if the book matches
     */
    private boolean matches(Book b, String q, String field) {
        String id = b.getId().toLowerCase(Locale.ROOT);
        String title = b.getTitle().toLowerCase(Locale.ROOT);
        String author = b.getAuthor().toLowerCase(Locale.ROOT);
        switch (field) {
            case SEARCH_BY_ID: return id.contains(q);
            case SEARCH_BY_TITLE: return title.contains(q);
            case SEARCH_BY_AUTHOR: return author.contains(q);
            default: return id.contains(q) || title.contains(q) || author.contains(q);
        }
    }

    /**
     * Combined search and filter used by the GUI search bar: partial, case-insensitive
     * search on one field, then filtering by status and category.
     *
     * @param query    the search text (may be empty)
     * @param searchBy "Book ID", "Title" or "Author"
     * @param status   status display name or "All"
     * @param category category display name or "All"
     * @return the matching books
     */
    public List<Book> searchAndFilter(String query, String searchBy, String status, String category) {
        List<Book> result = search(query, searchBy);
        result = filterByStatus(result, status);
        return filterByCategory(result, category);
    }

    /**
     * Filters books by status.
     *
     * @param bookList the list of books to filter
     * @param status   the status to filter by, or null/empty/"All" for no filter
     * @return filtered list of books
     */
    public List<Book> filterByStatus(List<Book> bookList, String status) {
        if (status == null || status.trim().isEmpty() || status.trim().equalsIgnoreCase(ALL)) {
            return new ArrayList<>(bookList);
        }
        BookStatus bs = BookStatus.fromString(status.trim());
        return bookList.stream().filter(b -> b.getStatus() == bs).collect(Collectors.toList());
    }

    /**
     * Filters books by category.
     *
     * @param bookList the list of books to filter
     * @param category the category to filter by, or null/empty/"All" for no filter
     * @return filtered list of books
     */
    public List<Book> filterByCategory(List<Book> bookList, String category) {
        if (category == null || category.trim().isEmpty() || category.trim().equalsIgnoreCase(ALL)) {
            return new ArrayList<>(bookList);
        }
        Category cat = Category.fromString(category.trim());
        return bookList.stream().filter(b -> b.getCategory() == cat).collect(Collectors.toList());
    }

    // ==================== ISSUE / RETURN / RESERVE ====================

    /**
     * Issues a book to a member. Only AVAILABLE books (or books RESERVED by the same member)
     * can be issued. Creates a loan record with issue and due dates.
     *
     * @param bookId    the book ID to issue
     * @param memberId  the member ID to issue to
     * @param loanDays  the loan period in days
     * @return the created Loan
     * @throws LibraryException if the book is not available for issue
     */
    public Loan issueBook(String bookId, String memberId, int loanDays) throws LibraryException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new LibraryException("Book ID cannot be empty.");
        }
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new LibraryException("Member ID cannot be empty.");
        }
        Book book = findById(bookId);
        if (book == null) {
            throw new LibraryException("Book with ID '" + bookId.trim() + "' not found.");
        }
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new LibraryException("Book '" + book.getTitle() + "' is already issued.");
        }
        if (book.getStatus() == BookStatus.RESERVED && !book.getReservedBy().equalsIgnoreCase(memberId.trim())) {
            throw new LibraryException("Book '" + book.getTitle() + "' is reserved by another member.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanDays > 0 ? loanDays : 14);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        loanCounter++;
        String loanId = "LOAN-" + String.format("%03d", loanCounter);
        Loan loan = new Loan(loanId, bookId.trim(), memberId.trim(),
                issueDate.format(fmt), dueDate.format(fmt));

        book.setStatus(BookStatus.ISSUED);
        book.setIssuedTo(memberId.trim());
        book.setDueDate(dueDate.format(fmt));
        book.setReservedBy("");

        loans.add(loan);
        return loan;
    }

    /**
     * Returns a book from an active loan. Sets the book status back to AVAILABLE.
     *
     * @param loanId the loan ID to return
     * @return the updated Loan
     * @throws LibraryException if the loan is not found or already returned
     */
    public Loan returnBook(String loanId) throws LibraryException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new LibraryException("Loan ID cannot be empty.");
        }
        Loan loan = findLoanById(loanId);
        if (loan == null) {
            throw new LibraryException("Loan with ID '" + loanId.trim() + "' not found.");
        }
        if (!loan.isActive()) {
            throw new LibraryException("This loan has already been returned.");
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        loan.setReturnDate(LocalDate.now().format(fmt));
        loan.setActive(false);

        Book book = findById(loan.getBookId());
        if (book != null) {
            book.setStatus(BookStatus.AVAILABLE);
            book.setIssuedTo("");
            book.setDueDate("");
        }

        return loan;
    }

    /**
     * Reserves an available book for a member.
     *
     * @param bookId   the book ID to reserve
     * @param memberId the member ID reserving the book
     * @throws LibraryException if the book is not available for reservation
     */
    public void reserveBook(String bookId, String memberId) throws LibraryException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new LibraryException("Book ID cannot be empty.");
        }
        Book book = findById(bookId);
        if (book == null) {
            throw new LibraryException("Book with ID '" + bookId.trim() + "' not found.");
        }
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new LibraryException("Book '" + book.getTitle() + "' is not available for reservation.");
        }
        book.setStatus(BookStatus.RESERVED);
        book.setReservedBy(memberId.trim());
    }

    /**
     * Cancels a reservation on a book.
     *
     * @param bookId   the book ID
     * @param memberId the member ID who made the reservation
     * @throws LibraryException if the book is not reserved by this member
     */
    public void cancelReservation(String bookId, String memberId) throws LibraryException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new LibraryException("Book ID cannot be empty.");
        }
        Book book = findById(bookId);
        if (book == null) {
            throw new LibraryException("Book with ID '" + bookId.trim() + "' not found.");
        }
        if (book.getStatus() != BookStatus.RESERVED) {
            throw new LibraryException("Book '" + book.getTitle() + "' is not reserved.");
        }
        if (!book.getReservedBy().equalsIgnoreCase(memberId.trim())) {
            throw new LibraryException("This book is reserved by a different member.");
        }
        book.setStatus(BookStatus.AVAILABLE);
        book.setReservedBy("");
    }

    // ==================== LOANS ====================

    /**
     * Returns all loans.
     *
     * @return list of all loans
     */
    public List<Loan> getAllLoans() {
        return Collections.unmodifiableList(loans);
    }

    /**
     * Returns all active loans.
     *
     * @return list of active loans
     */
    public List<Loan> getActiveLoans() {
        return loans.stream().filter(Loan::isActive).collect(Collectors.toList());
    }

    /**
     * Returns active loans for a specific member.
     *
     * @param memberId the member ID
     * @return list of active loans for the member
     */
    public List<Loan> getActiveLoansByMember(String memberId) {
        if (memberId == null) return new ArrayList<>();
        return loans.stream()
                .filter(l -> l.isActive() && l.getMemberId().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Finds a loan by its ID.
     *
     * @param loanId the loan ID
     * @return the Loan if found, or null
     */
    public Loan findLoanById(String loanId) {
        if (loanId == null) return null;
        String trimmed = loanId.trim();
        for (Loan loan : loans) {
            if (loan.getLoanId().equalsIgnoreCase(trimmed)) {
                return loan;
            }
        }
        return null;
    }

    // ==================== OVERDUE ====================

    /**
     * Returns all overdue loans (active loans whose due date is before today).
     *
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return loans.stream()
                .filter(l -> l.isActive() && LocalDate.parse(l.getDueDate(), fmt).isBefore(today))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the number of days a loan is overdue.
     *
     * @param loan the loan to check
     * @return the number of days overdue (0 if not overdue)
     */
    public long getDaysOverdue(Loan loan) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate due = LocalDate.parse(loan.getDueDate(), fmt);
        long days = ChronoUnit.DAYS.between(due, today);
        return days > 0 ? days : 0;
    }

    // ==================== STATISTICS ====================

    /**
     * Returns a map of library statistics.
     * Keys: totalBooks, availableBooks, issuedBooks, reservedBooks, totalMembers, overdueCount.
     * Note: totalMembers should be set by the caller since MemberService manages members.
     *
     * @return map of statistic name to value
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("totalBooks", books.size());
        stats.put("availableBooks", (int) books.stream().filter(b -> b.getStatus() == BookStatus.AVAILABLE).count());
        stats.put("issuedBooks", (int) books.stream().filter(b -> b.getStatus() == BookStatus.ISSUED).count());
        stats.put("reservedBooks", (int) books.stream().filter(b -> b.getStatus() == BookStatus.RESERVED).count());
        stats.put("overdueCount", getOverdueLoans().size());
        return stats;
    }

    // ==================== VALIDATION ====================

    /**
     * Validates a book's required fields.
     *
     * @param book the book to validate
     * @throws LibraryException if any required field is empty or null
     */
    private void validateBook(Book book) throws LibraryException {
        if (book.getId() == null || book.getId().trim().isEmpty()) {
            throw new LibraryException("Book ID cannot be empty.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new LibraryException("Book title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new LibraryException("Book author cannot be empty.");
        }
        if (book.getCategory() == null) {
            throw new LibraryException("Book category cannot be empty.");
        }
        // Trim the fields
        book.setId(book.getId().trim());
        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
    }
}
