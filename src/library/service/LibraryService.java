package library.service;

import library.exception.LibraryException;
import library.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    /** Member service used to validate member IDs and count members (may be null). */
    private MemberService memberService;

    /**
     * Connects the member service so issuing validates member IDs and statistics include members.
     *
     * @param memberService the member service
     */
    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    /** Transaction log that records every change (may be null). */
    private TransactionService transactionService;
    /** Email of the logged-in user, written to the "Performed By" column. */
    private String actor = "system";

    /**
     * Connects the transaction service so every operation is logged.
     *
     * @param transactionService the transaction service
     */
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Sets the email of the user performing subsequent operations.
     *
     * @param actorEmail the logged-in user's email
     */
    public void setActor(String actorEmail) {
        this.actor = (actorEmail == null || actorEmail.trim().isEmpty()) ? "system" : actorEmail.trim();
    }

    /**
     * Returns the email of the user currently performing operations.
     *
     * @return the actor email
     */
    public String getActor() {
        return actor;
    }

    /**
     * Writes a transaction to the log if a transaction service is connected.
     *
     * @param type     transaction type
     * @param bookId   book ID
     * @param memberId member ID
     * @param details  details text
     */
    private void log(TransactionType type, String bookId, String memberId, String details) {
        if (transactionService != null) {
            transactionService.record(type, bookId, memberId, actor, details);
        }
    }

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
        log(TransactionType.ADD, book.getId(), "", "Added '" + book.getTitle() + "' by " + book.getAuthor()
                + " (" + book.getCategory().getDisplayName() + ")");
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
        log(TransactionType.UPDATE, book.getId(), "", "Updated to '" + book.getTitle() + "' by "
                + book.getAuthor() + " (" + category.getDisplayName() + ")");
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
        log(TransactionType.DELETE, book.getId(), "", "Deleted '" + book.getTitle() + "'");
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
        BookStatus old = book.getStatus();
        book.setStatus(status);
        book.setIssuedTo("");
        book.setDueDate("");
        book.setReservedBy("");
        log(TransactionType.STATUS_CHANGE, book.getId(), "",
                "Status changed from " + old.getDisplayName() + " to " + status.getDisplayName());
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

    /** Default loan period in days. */
    public static final int DEFAULT_LOAN_DAYS = 14;
    /** Maximum allowed loan period in days. */
    public static final int MAX_LOAN_DAYS = 365;
    /** Date format used for all stored dates. */
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Issues a book to a member. Only AVAILABLE books (or books RESERVED by the same member)
     * can be issued. Creates a loan record with issue date (today) and due date
     * (today + loan period) using {@link LocalDate}.
     *
     * @param bookId    the book ID to issue
     * @param memberId  the member ID to issue to
     * @param loanDays  the loan period in days (1 to {@value #MAX_LOAN_DAYS})
     * @return the created Loan
     * @throws LibraryException if input is invalid or the book is not available for issue
     */
    public Loan issueBook(String bookId, String memberId, int loanDays) throws LibraryException {
        Book book = requireBook(bookId);
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new LibraryException("Member ID cannot be empty.");
        }
        String mid = memberId.trim();
        if (memberService != null) {
            mid = memberService.requireMember(mid).getMemberId();
        }
        if (loanDays < 1 || loanDays > MAX_LOAN_DAYS) {
            throw new LibraryException("Loan period must be between 1 and " + MAX_LOAN_DAYS + " days.");
        }
        if (book.getStatus() == BookStatus.ISSUED || hasActiveLoan(book.getId())) {
            throw new LibraryException("Book '" + book.getTitle() + "' is already issued.");
        }
        if (book.getStatus() == BookStatus.RESERVED && !book.getReservedBy().equalsIgnoreCase(mid)) {
            throw new LibraryException("Book '" + book.getTitle() + "' is reserved by another member.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanDays);
        Loan loan = new Loan(nextLoanId(), book.getId(), mid,
                issueDate.format(DATE_FMT), dueDate.format(DATE_FMT));

        book.setStatus(BookStatus.ISSUED);
        book.setIssuedTo(mid);
        book.setDueDate(dueDate.format(DATE_FMT));
        book.setReservedBy("");

        loans.add(loan);
        log(TransactionType.ISSUE, book.getId(), mid, "Issued '" + book.getTitle() + "' for " + loanDays
                + " days, due " + loan.getDueDate());
        return loan;
    }

    /**
     * Returns a book from an active loan. Records the return date and
     * automatically sets the book status back to AVAILABLE.
     *
     * @param loanId the loan ID to return
     * @return the updated Loan
     * @throws LibraryException if the loan is not found or already returned
     */
    public Loan returnBook(String loanId) throws LibraryException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new LibraryException("Please select a loan to return.");
        }
        Loan loan = findLoanById(loanId);
        if (loan == null) {
            throw new LibraryException("Loan with ID '" + loanId.trim() + "' not found.");
        }
        if (!loan.isActive()) {
            throw new LibraryException("This loan has already been returned.");
        }

        loan.setReturnDate(LocalDate.now().format(DATE_FMT));
        loan.setActive(false);

        long late = getDaysOverdue(loan);
        Book book = findById(loan.getBookId());
        if (book != null) {
            book.setStatus(BookStatus.AVAILABLE);
            book.setIssuedTo("");
            book.setDueDate("");
            book.setReservedBy("");
        }
        log(TransactionType.RETURN, loan.getBookId(), loan.getMemberId(),
                "Returned" + (book != null ? " '" + book.getTitle() + "'" : "")
                        + (late > 0 ? " (" + late + " days late)" : " on time"));
        return loan;
    }

    /**
     * Returns a book using its book ID (finds the active loan automatically).
     *
     * @param bookId the book ID
     * @return the closed loan
     * @throws LibraryException if the book does not exist or is not on loan
     */
    public Loan returnBookByBookId(String bookId) throws LibraryException {
        Book book = requireBook(bookId);
        Loan loan = findActiveLoanForBook(book.getId());
        if (loan == null) {
            throw new LibraryException("Book '" + book.getTitle() + "' is not currently issued.");
        }
        return returnBook(loan.getLoanId());
    }

    /**
     * Reserves an available book for a member. Status becomes RESERVED.
     *
     * @param bookId   the book ID to reserve
     * @param memberId the member ID reserving the book
     * @return the reserved book
     * @throws LibraryException if input is empty or the book is not AVAILABLE
     */
    public Book reserveBook(String bookId, String memberId) throws LibraryException {
        Book book = requireBook(bookId);
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new LibraryException("Member ID cannot be empty.");
        }
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new LibraryException("Book '" + book.getTitle() + "' is "
                    + book.getStatus().getDisplayName() + " and cannot be reserved. Only Available books can be reserved.");
        }
        book.setStatus(BookStatus.RESERVED);
        book.setReservedBy(memberId.trim());
        log(TransactionType.RESERVE, book.getId(), memberId.trim(), "Reserved '" + book.getTitle() + "'");
        return book;
    }

    /**
     * Cancels a member's own reservation on a book. Status returns to AVAILABLE.
     *
     * @param bookId   the book ID
     * @param memberId the member ID who made the reservation
     * @return the book whose reservation was cancelled
     * @throws LibraryException if the book is not reserved by this member
     */
    public Book cancelReservation(String bookId, String memberId) throws LibraryException {
        Book book = requireBook(bookId);
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new LibraryException("Member ID cannot be empty.");
        }
        if (book.getStatus() != BookStatus.RESERVED) {
            throw new LibraryException("Book '" + book.getTitle() + "' is not reserved.");
        }
        if (!book.getReservedBy().equalsIgnoreCase(memberId.trim())) {
            throw new LibraryException("You can only cancel your own reservations.");
        }
        book.setStatus(BookStatus.AVAILABLE);
        book.setReservedBy("");
        log(TransactionType.CANCEL_RESERVATION, book.getId(), memberId.trim(),
                "Cancelled reservation of '" + book.getTitle() + "'");
        return book;
    }

    /**
     * Returns books currently reserved by a member.
     *
     * @param memberId the member ID
     * @return list of reserved books
     */
    public List<Book> getReservedBooksByMember(String memberId) {
        if (memberId == null) return new ArrayList<>();
        return books.stream()
                .filter(b -> b.getStatus() == BookStatus.RESERVED && b.getReservedBy().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Generates the next unique loan ID (LOAN-001, LOAN-002, ...), based on the highest existing number.
     *
     * @return the next loan ID
     */
    private String nextLoanId() {
        int max = loanCounter;
        for (Loan l : loans) {
            max = Math.max(max, numericSuffix(l.getLoanId()));
        }
        loanCounter = max + 1;
        return String.format("LOAN-%03d", loanCounter);
    }

    /**
     * Extracts the trailing number of an ID such as "LOAN-012".
     *
     * @param id the ID
     * @return the numeric suffix, or 0 if none
     */
    static int numericSuffix(String id) {
        if (id == null) return 0;
        String digits = id.replaceAll("^.*?(\\d+)$", "$1");
        try {
            return digits.matches("\\d+") ? Integer.parseInt(digits) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
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
        return loans.stream().filter(this::isOverdue).collect(Collectors.toList());
    }

    /**
     * Returns overdue loans belonging to one member.
     *
     * @param memberId the member ID
     * @return the member's overdue loans
     */
    public List<Loan> getOverdueLoansByMember(String memberId) {
        if (memberId == null) return new ArrayList<>();
        return getOverdueLoans().stream()
                .filter(l -> l.getMemberId().equalsIgnoreCase(memberId.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether a loan is overdue: still active and its due date is before today.
     *
     * @param loan the loan
     * @return true if overdue
     */
    public boolean isOverdue(Loan loan) {
        if (loan == null || !loan.isActive()) return false;
        LocalDate due = parseDate(loan.getDueDate());
        return due != null && due.isBefore(LocalDate.now());
    }

    /**
     * Calculates the number of days a loan is overdue.
     *
     * @param loan the loan to check
     * @return the number of days overdue (0 if not overdue)
     */
    public long getDaysOverdue(Loan loan) {
        if (loan == null) return 0;
        LocalDate due = parseDate(loan.getDueDate());
        if (due == null) return 0;
        long days = ChronoUnit.DAYS.between(due, LocalDate.now());
        return days > 0 ? days : 0;
    }

    /**
     * Parses a yyyy-MM-dd date safely.
     *
     * @param text the date text
     * @return the parsed date, or null if the text is empty or invalid
     */
    private static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ==================== STATISTICS ====================

    /** Statistics key: total number of books. */
    public static final String STAT_TOTAL = "totalBooks";
    /** Statistics key: number of available books. */
    public static final String STAT_AVAILABLE = "availableBooks";
    /** Statistics key: number of issued books. */
    public static final String STAT_ISSUED = "issuedBooks";
    /** Statistics key: number of reserved books. */
    public static final String STAT_RESERVED = "reservedBooks";
    /** Statistics key: number of registered members. */
    public static final String STAT_MEMBERS = "totalMembers";
    /** Statistics key: number of overdue loans. */
    public static final String STAT_OVERDUE = "overdueCount";

    /**
     * Returns a map of library statistics.
     * Keys: totalBooks, availableBooks, issuedBooks, reservedBooks, totalMembers, overdueCount.
     * totalMembers is taken from the connected {@link MemberService} (0 if none is connected).
     *
     * @return map of statistic name to value
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put(STAT_TOTAL, books.size());
        stats.put(STAT_AVAILABLE, countByStatus(BookStatus.AVAILABLE));
        stats.put(STAT_ISSUED, countByStatus(BookStatus.ISSUED));
        stats.put(STAT_RESERVED, countByStatus(BookStatus.RESERVED));
        stats.put(STAT_MEMBERS, memberService != null ? memberService.count() : 0);
        stats.put(STAT_OVERDUE, getOverdueLoans().size());
        return stats;
    }

    /**
     * Counts books with the given status.
     *
     * @param status the status
     * @return number of books with that status
     */
    public int countByStatus(BookStatus status) {
        return (int) books.stream().filter(b -> b.getStatus() == status).count();
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
