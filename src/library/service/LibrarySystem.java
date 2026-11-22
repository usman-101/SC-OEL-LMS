package library.service;

import library.exception.LibraryException;
import library.model.Book;
import library.model.Category;
import library.model.Member;
import library.storage.DataStore;

import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Wires all services together and connects them to the {@link DataStore}.
 * On first run (no data files) it seeds sample data: books in several categories,
 * sample members and one overdue loan for demonstrating overdue tracking.
 * A LibrarySystem created without a data folder works purely in memory (used by tests).
 *
 * @author Usman
 */
public class LibrarySystem {

    /** Book, loan and reservation operations. */
    private final LibraryService libraryService;
    /** Member management. */
    private final MemberService memberService;
    /** Sign up / sign in. */
    private final AuthService authService;
    /** Transaction log. */
    private final TransactionService transactionService;
    /** File storage, or null for in-memory mode. */
    private final DataStore dataStore;

    /**
     * Creates a system backed by files in the given folder.
     * Call {@link #load()} afterwards to read data (or seed it on first run).
     *
     * @param dataDir the data folder, or null for in-memory mode with no persistence
     */
    public LibrarySystem(Path dataDir) {
        this.transactionService = new TransactionService();
        this.memberService = new MemberService();
        this.libraryService = new LibraryService();
        this.libraryService.setMemberService(memberService);
        this.libraryService.setTransactionService(transactionService);
        this.memberService.setLibraryService(libraryService);
        this.authService = new AuthService(memberService);
        this.dataStore = dataDir == null ? null : new DataStore(dataDir);
    }

    /**
     * Creates an empty in-memory system with no persistence (useful for tests).
     *
     * @return a new in-memory system
     */
    public static LibrarySystem inMemory() {
        return new LibrarySystem(null);
    }

    /**
     * Loads saved data from the data folder, or seeds and saves sample data when no files exist.
     *
     * @throws LibraryException if reading or writing files fails
     */
    public void load() throws LibraryException {
        if (dataStore == null) return;
        if (!dataStore.hasData()) {
            seedSampleData();
            save();
            return;
        }
        authService.setUsers(dataStore.loadUsers());
        memberService.setMembers(dataStore.loadMembers());
        libraryService.setBooks(dataStore.loadBooks());
        libraryService.setLoans(dataStore.loadLoans());
        transactionService.setTransactions(dataStore.loadTransactions());
    }

    /**
     * Saves all data to the data folder (no-op in in-memory mode).
     *
     * @throws LibraryException if writing fails
     */
    public void save() throws LibraryException {
        if (dataStore == null) return;
        dataStore.saveUsers(authService.getAllUsers());
        dataStore.saveMembers(memberService.getAllMembers());
        dataStore.saveBooks(libraryService.getAllBooks());
        dataStore.saveLoans(libraryService.getAllLoans());
        dataStore.saveTransactions(transactionService.getAllChronological());
    }

    /**
     * Seeds sample data: default admin and student accounts, 14 books across categories,
     * 2 members and one overdue loan.
     *
     * @throws LibraryException if seeding fails (should not happen with the built-in data)
     */
    public void seedSampleData() throws LibraryException {
        String previousActor = libraryService.getActor();
        libraryService.setActor("system");
        Object[][] sample = {
                {"B001", "Clean Code", "Robert C. Martin", Category.PROGRAMMING},
                {"B002", "Effective Java", "Joshua Bloch", Category.PROGRAMMING},
                {"B003", "Head First Java", "Kathy Sierra", Category.PROGRAMMING},
                {"B004", "Introduction to Algorithms", "Thomas H. Cormen", Category.PROGRAMMING},
                {"B005", "A Brief History of Time", "Stephen Hawking", Category.SCIENCE},
                {"B006", "The Selfish Gene", "Richard Dawkins", Category.SCIENCE},
                {"B007", "Cosmos", "Carl Sagan", Category.SCIENCE},
                {"B008", "Calculus: Early Transcendentals", "James Stewart", Category.MATHEMATICS},
                {"B009", "Linear Algebra Done Right", "Sheldon Axler", Category.MATHEMATICS},
                {"B010", "Discrete Mathematics", "Kenneth H. Rosen", Category.MATHEMATICS},
                {"B011", "Pride and Prejudice", "Jane Austen", Category.LITERATURE},
                {"B012", "To Kill a Mockingbird", "Harper Lee", Category.LITERATURE},
                {"B013", "Sapiens", "Yuval Noah Harari", Category.HISTORY},
                {"B014", "Computer Networks", "Andrew S. Tanenbaum", Category.TECHNOLOGY},
        };
        for (Object[] row : sample) {
            libraryService.addBook(new Book((String) row[0], (String) row[1], (String) row[2], (Category) row[3]));
        }

        Member ali = memberService.registerMember("Ali Khan", "0300-1234567", "ali@gmail.com");
        Member sara = memberService.registerMember("Sara Ahmed", "0321-7654321", "sara.ahmed@gmail.com");

        // Default accounts (the student links to the existing member record by email).
        authService.signUp("Usman", "usman.admin@library.com", "Admin@123", "Admin@123", AuthService.ADMIN_ACCESS_CODE);
        authService.signUp("Ali Khan", "ali@gmail.com", "Student@123", "Student@123", "");

        // The sample student has one active loan that is already overdue (issued 30 days ago for 14 days).
        libraryService.issueBook("B004", ali.getMemberId(), LibraryService.DEFAULT_LOAN_DAYS,
                LocalDate.now().minusDays(30));

        // One normal (not overdue) loan for the second member.
        libraryService.issueBook("B001", sara.getMemberId(), LibraryService.DEFAULT_LOAN_DAYS);
        libraryService.setActor(previousActor);
    }

    /**
     * Returns the library (book/loan) service.
     *
     * @return the library service
     */
    public LibraryService getLibraryService() {
        return libraryService;
    }

    /**
     * Returns the authentication service.
     *
     * @return the auth service
     */
    public AuthService getAuthService() {
        return authService;
    }

    /**
     * Returns the member service.
     *
     * @return the member service
     */
    public MemberService getMemberService() {
        return memberService;
    }

    /**
     * Returns the transaction service.
     *
     * @return the transaction service
     */
    public TransactionService getTransactionService() {
        return transactionService;
    }

    /**
     * Returns the data store, or null in in-memory mode.
     *
     * @return the data store
     */
    public DataStore getDataStore() {
        return dataStore;
    }
}
