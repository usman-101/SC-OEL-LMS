package library;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BookStatus;
import library.model.Category;
import library.model.Loan;
import library.model.Member;
import library.model.Role;
import library.model.Transaction;
import library.model.TransactionType;
import library.model.User;
import library.service.AuthService;
import library.service.LibraryService;
import library.service.LibrarySystem;
import library.service.MemberService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Plain-Java test runner (no JUnit) for the service layer. Every test prints PASS or FAIL
 * with the expected and actual values, followed by a summary. All tests use fresh in-memory
 * data (or a temporary folder for the persistence test) and never touch the real data/ folder.
 *
 * @author Usman
 */
public final class LibraryServiceTest {

    /** Number of passed checks. */
    private static int passed;
    /** Number of failed checks. */
    private static int failed;
    /** Running test number. */
    private static int number;

    /**
     * Not instantiable.
     */
    private LibraryServiceTest() {
    }

    /**
     * An action that may throw, used for expected-error tests.
     *
     * @author Usman
     */
    @FunctionalInterface
    private interface Action {
        /**
         * Runs the action.
         *
         * @throws Exception any exception
         */
        void run() throws Exception;
    }

    /**
     * Runs all tests and prints a summary.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println(" Library Book Management System - Service Test Results");
        System.out.println(" Run date: " + LocalDate.now() + "   Java: " + System.getProperty("java.version"));
        System.out.println("==============================================================");

        section("Required test cases (lab manual)");
        testAddValidBook();
        testAddEmptyTitle();
        testAddDuplicateId();
        testViewAllBooks();
        testViewEmptyLibrary();
        testUpdateExistingBook();
        testUpdateNonExistingBook();
        testDeleteExistingBook();

        section("Additional validation tests");
        testAddEmptyAuthor();
        testAddEmptyId();
        testSetStatus();
        testDeleteNonExisting();
        testDeleteIssuedBook();

        section("Issue / return / overdue");
        testIssueBook();
        testIssueAlreadyIssued();
        testReturnBook();
        testOverdueDetection();

        section("Search, filter and statistics");
        testSearch();
        testFilterByStatus();
        testStatistics();

        section("Members");
        testMemberRegistration();
        testDeleteMemberWithLoans();

        section("Authentication and roles");
        testSignUpStudent();
        testSignUpDuplicateEmail();
        testWeakPassword();
        testAdminEmailDetection();
        testStudentEmailDetection();
        testWrongPassword();
        testAdminWrongCode();

        section("Reservations, history and persistence");
        testReserveAndCancel();
        testTransactionHistory();
        testPersistenceRoundTrip();

        System.out.println();
        System.out.println("==============================================================");
        System.out.printf(" SUMMARY: %d checks, %d PASSED, %d FAILED%n", passed + failed, passed, failed);
        System.out.println(failed == 0 ? " RESULT: ALL TESTS PASSED" : " RESULT: SOME TESTS FAILED");
        System.out.println("==============================================================");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ==================== helpers ====================

    /**
     * Prints a section heading.
     *
     * @param title the heading
     */
    private static void section(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    /**
     * Records one check, printing PASS/FAIL with expected and actual values.
     *
     * @param name     test description
     * @param expected expected value
     * @param actual   actual value
     */
    private static void check(String name, Object expected, Object actual) {
        number++;
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        if (ok) passed++; else failed++;
        System.out.printf("[%s] %02d. %s%n        expected: %s%n        actual:   %s%n",
                ok ? "PASS" : "FAIL", number, name, expected, actual);
    }

    /**
     * Checks that an action throws a {@link LibraryException} whose message contains a fragment.
     *
     * @param name     test description
     * @param fragment text expected in the error message
     * @param action   the action
     */
    private static void expectError(String name, String fragment, Action action) {
        String actual;
        try {
            action.run();
            actual = "no exception";
        } catch (LibraryException e) {
            actual = e.getMessage().contains(fragment) ? "LibraryException containing \"" + fragment + "\""
                    : "LibraryException: " + e.getMessage();
        } catch (Exception e) {
            actual = "unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        check(name, "LibraryException containing \"" + fragment + "\"", actual);
    }

    /**
     * Creates a fresh in-memory system with three books.
     *
     * @return the system
     * @throws LibraryException never for valid sample data
     */
    private static LibrarySystem sample() throws LibraryException {
        LibrarySystem s = LibrarySystem.inMemory();
        LibraryService lib = s.getLibraryService();
        lib.addBook(new Book("B1", "Clean Code", "Robert Martin", Category.PROGRAMMING));
        lib.addBook(new Book("B2", "Cosmos", "Carl Sagan", Category.SCIENCE));
        lib.addBook(new Book("B3", "Calculus", "James Stewart", Category.MATHEMATICS));
        s.getMemberService().registerMember("Ali Khan", "0300-1234567", "ali@gmail.com");
        s.getMemberService().registerMember("Sara Ahmed", "", "sara@gmail.com");
        return s;
    }

    /**
     * Runs a test body, converting unexpected exceptions into a failure.
     *
     * @param name test name
     * @param body test body
     */
    private static void run(String name, Action body) {
        try {
            body.run();
        } catch (Exception e) {
            check(name, "no unexpected exception", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // ==================== required tests ====================

    /** Test 1: add a valid book. */
    private static void testAddValidBook() {
        run("Add a valid book", () -> {
            LibrarySystem s = LibrarySystem.inMemory();
            s.getLibraryService().addBook(new Book("B100", "Effective Java", "Joshua Bloch", Category.PROGRAMMING));
            Book b = s.getLibraryService().findById("B100");
            check("Add a valid book -> book is added (status Available)", "Effective Java / Available",
                    b == null ? "null" : b.getTitle() + " / " + b.getStatus());
        });
    }

    /** Test 2: empty title is rejected. */
    private static void testAddEmptyTitle() {
        LibrarySystem s = LibrarySystem.inMemory();
        expectError("Add a book with an empty title -> validation error", "title cannot be empty",
                () -> s.getLibraryService().addBook(new Book("B1", "   ", "Author", Category.OTHER)));
    }

    /** Test 3: duplicate ID is rejected. */
    private static void testAddDuplicateId() {
        run("Duplicate ID", () -> {
            LibrarySystem s = sample();
            expectError("Add a book with a duplicate ID -> error", "already exists",
                    () -> s.getLibraryService().addBook(new Book("b1", "Other", "Someone", Category.OTHER)));
        });
    }

    /** Test 4: view all books. */
    private static void testViewAllBooks() {
        run("View all", () -> {
            LibrarySystem s = sample();
            List<Book> all = s.getLibraryService().getAllBooks();
            check("View all books -> all books returned", "3 books [B1, B2, B3]",
                    all.size() + " books " + all.stream().map(Book::getId).toList());
        });
    }

    /** Test 5: view an empty library. */
    private static void testViewEmptyLibrary() {
        LibraryService lib = LibrarySystem.inMemory().getLibraryService();
        check("View an empty library -> empty list, isEmpty() true (GUI shows \"The library is empty...\")",
                "isEmpty=true, size=0", "isEmpty=" + lib.isEmpty() + ", size=" + lib.getAllBooks().size());
    }

    /** Test 6: update an existing book. */
    private static void testUpdateExistingBook() {
        run("Update existing", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().updateBook("B2", " Cosmos (2nd Ed.) ", "Carl Sagan", Category.HISTORY);
            Book b = s.getLibraryService().findById("B2");
            check("Update an existing book -> details updated (and trimmed)", "Cosmos (2nd Ed.) / History",
                    b.getTitle() + " / " + b.getCategory());
        });
    }

    /** Test 7: update a non-existing book. */
    private static void testUpdateNonExistingBook() {
        run("Update non-existing", () -> {
            LibrarySystem s = sample();
            expectError("Update a non-existing book -> handled gracefully (no crash)", "not found",
                    () -> s.getLibraryService().updateBook("X999", "T", "A", Category.OTHER));
        });
    }

    /** Test 8: delete an existing book. */
    private static void testDeleteExistingBook() {
        run("Delete existing", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().deleteBook("B3");
            check("Delete an existing book -> book removed", "findById=null, size=2",
                    "findById=" + s.getLibraryService().findById("B3") + ", size=" + s.getLibraryService().getAllBooks().size());
        });
    }

    // ==================== extra validation ====================

    /** Empty author is rejected. */
    private static void testAddEmptyAuthor() {
        LibrarySystem s = LibrarySystem.inMemory();
        expectError("Add a book with an empty author -> validation error", "author cannot be empty",
                () -> s.getLibraryService().addBook(new Book("B1", "Title", "", Category.OTHER)));
    }

    /** Empty ID is rejected. */
    private static void testAddEmptyId() {
        LibrarySystem s = LibrarySystem.inMemory();
        expectError("Add a book with an empty ID -> validation error", "ID cannot be empty",
                () -> s.getLibraryService().addBook(new Book("", "Title", "Author", Category.OTHER)));
    }

    /** Status can be changed manually. */
    private static void testSetStatus() {
        run("Set status", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().setStatus("B2", BookStatus.RESERVED);
            check("Set status Available -> Reserved", "Reserved", s.getLibraryService().findById("B2").getStatus());
            expectError("Set the same status again -> error", "already Reserved",
                    () -> s.getLibraryService().setStatus("B2", BookStatus.RESERVED));
        });
    }

    /** Deleting a non-existing book is handled. */
    private static void testDeleteNonExisting() {
        run("Delete non-existing", () -> {
            LibrarySystem s = sample();
            expectError("Delete a non-existing book -> handled gracefully", "not found",
                    () -> s.getLibraryService().deleteBook("NOPE"));
        });
    }

    /** Issued books cannot be deleted. */
    private static void testDeleteIssuedBook() {
        run("Delete issued", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().issueBook("B1", "MEM-001", 14);
            expectError("Delete an issued book -> rejected", "currently issued",
                    () -> s.getLibraryService().deleteBook("B1"));
        });
    }

    // ==================== issue / return / overdue ====================

    /** Issuing records dates and status. */
    private static void testIssueBook() {
        run("Issue", () -> {
            LibrarySystem s = sample();
            Loan loan = s.getLibraryService().issueBook("B1", "MEM-001", 14);
            Book b = s.getLibraryService().findById("B1");
            String expected = "ISSUED to MEM-001, issued " + LocalDate.now() + ", due " + LocalDate.now().plusDays(14);
            check("Issue a book -> status Issued, issue and due date recorded", expected,
                    b.getStatus().name() + " to " + b.getIssuedTo() + ", issued " + loan.getIssueDate()
                            + ", due " + loan.getDueDate());
            expectError("Issue to a non-existing member -> error", "not found",
                    () -> s.getLibraryService().issueBook("B2", "MEM-999", 14));
        });
    }

    /** An issued book cannot be issued again. */
    private static void testIssueAlreadyIssued() {
        run("Issue twice", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().issueBook("B1", "MEM-001", 14);
            expectError("Issue an already issued book -> error", "already issued",
                    () -> s.getLibraryService().issueBook("B1", "MEM-002", 7));
        });
    }

    /** Returning makes the book available. */
    private static void testReturnBook() {
        run("Return", () -> {
            LibrarySystem s = sample();
            Loan loan = s.getLibraryService().issueBook("B1", "MEM-001", 14);
            s.getLibraryService().returnBook(loan.getLoanId());
            check("Return a book -> status automatically Available, return date recorded",
                    "Available, returned " + LocalDate.now() + ", active=false",
                    s.getLibraryService().findById("B1").getStatus() + ", returned " + loan.getReturnDate()
                            + ", active=" + loan.isActive());
            expectError("Return the same loan twice -> error", "already been returned",
                    () -> s.getLibraryService().returnBook(loan.getLoanId()));
        });
    }

    /** Overdue loans are detected. */
    private static void testOverdueDetection() {
        run("Overdue", () -> {
            LibrarySystem s = sample();
            LibraryService lib = s.getLibraryService();
            Loan late = lib.issueBook("B1", "MEM-001", 14, LocalDate.now().minusDays(20));
            lib.issueBook("B2", "MEM-002", 14);
            List<Loan> overdue = lib.getOverdueLoans();
            check("Overdue detection -> only the late loan, 6 days overdue", "1 overdue: B1, 6 days",
                    overdue.size() + " overdue: " + overdue.get(0).getBookId() + ", " + lib.getDaysOverdue(late) + " days");
        });
    }

    // ==================== search / filter / statistics ====================

    /** Search by ID, title and author. */
    private static void testSearch() {
        run("Search", () -> {
            LibraryService lib = sample().getLibraryService();
            check("Search by ID 'b2' (case-insensitive)", "[B2]",
                    lib.search("b2", LibraryService.SEARCH_BY_ID).stream().map(Book::getId).toList());
            check("Search by title 'CAL' (partial)", "[B3]",
                    lib.search("CAL", LibraryService.SEARCH_BY_TITLE).stream().map(Book::getId).toList());
            check("Search by author 'sagan'", "[B2]",
                    lib.search("sagan", LibraryService.SEARCH_BY_AUTHOR).stream().map(Book::getId).toList());
        });
    }

    /** Filter by status (and combined with category). */
    private static void testFilterByStatus() {
        run("Filter", () -> {
            LibrarySystem s = sample();
            LibraryService lib = s.getLibraryService();
            lib.issueBook("B1", "MEM-001", 14);
            check("Filter by status Issued", "[B1]",
                    lib.filterByStatus(lib.getAllBooks(), "Issued").stream().map(Book::getId).toList());
            check("Search + filter: status Available, category Science", "[B2]",
                    lib.searchAndFilter("", "Title", "Available", "Science").stream().map(Book::getId).toList());
        });
    }

    /** Statistics counts. */
    private static void testStatistics() {
        run("Statistics", () -> {
            LibrarySystem s = sample();
            LibraryService lib = s.getLibraryService();
            lib.issueBook("B1", "MEM-001", 14, LocalDate.now().minusDays(30));
            lib.reserveBook("B2", "MEM-002");
            Map<String, Integer> st = lib.getStatistics();
            check("Statistics counts correct", "total=3 available=1 issued=1 reserved=1 members=2 overdue=1",
                    "total=" + st.get(LibraryService.STAT_TOTAL) + " available=" + st.get(LibraryService.STAT_AVAILABLE)
                            + " issued=" + st.get(LibraryService.STAT_ISSUED) + " reserved=" + st.get(LibraryService.STAT_RESERVED)
                            + " members=" + st.get(LibraryService.STAT_MEMBERS) + " overdue=" + st.get(LibraryService.STAT_OVERDUE));
        });
    }

    // ==================== members ====================

    /** Member registration and duplicate email. */
    private static void testMemberRegistration() {
        run("Members", () -> {
            MemberService ms = LibrarySystem.inMemory().getMemberService();
            Member a = ms.registerMember("  Usman  ", "0300-1112223", "usman@gmail.com");
            Member b = ms.registerMember("Hina", "", "hina@gmail.com");
            check("Member registration -> auto IDs MEM-001, MEM-002 (name trimmed)", "MEM-001 Usman, MEM-002 Hina",
                    a.getMemberId() + " " + a.getName() + ", " + b.getMemberId() + " " + b.getName());
            expectError("Register a duplicate member email -> rejected", "already exists",
                    () -> ms.registerMember("Other", "", "USMAN@gmail.com"));
            expectError("Register a member with an empty name -> rejected", "name cannot be empty",
                    () -> ms.registerMember(" ", "", "x@gmail.com"));
        });
    }

    /** Members with active loans cannot be deleted. */
    private static void testDeleteMemberWithLoans() {
        run("Delete member", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().issueBook("B1", "MEM-001", 14);
            expectError("Delete a member with active loans -> rejected", "active loans",
                    () -> s.getMemberService().deleteMember("MEM-001"));
        });
    }

    // ==================== auth ====================

    /** Student sign-up creates a member record and hashes the password. */
    private static void testSignUpStudent() {
        run("Sign up", () -> {
            LibrarySystem s = LibrarySystem.inMemory();
            User u = s.getAuthService().signUp("Bilal", "bilal@gmail.com", "secret1", "secret1", "");
            check("Sign up valid student -> STUDENT with member MEM-001", "STUDENT MEM-001 member=Bilal",
                    u.getRole().name() + " " + u.getMemberId() + " member=" + s.getMemberService().findById(u.getMemberId()).getName());
            check("Password stored as SHA-256 hash, not plain text", "64 hex chars, not 'secret1'",
                    (u.getPasswordHash().matches("[0-9a-f]{64}") ? "64 hex chars" : u.getPasswordHash())
                            + (u.getPasswordHash().equals("secret1") ? ", plain!" : ", not 'secret1'"));
        });
    }

    /** Duplicate email is rejected. */
    private static void testSignUpDuplicateEmail() {
        run("Duplicate sign up", () -> {
            AuthService auth = LibrarySystem.inMemory().getAuthService();
            auth.signUp("Bilal", "bilal@gmail.com", "secret1", "secret1", "");
            expectError("Sign up with a duplicate email -> rejected", "already exists",
                    () -> auth.signUp("Bilal 2", "Bilal@Gmail.com", "secret2", "secret2", ""));
        });
    }

    /** Weak password and mismatch are rejected. */
    private static void testWeakPassword() {
        AuthService auth = LibrarySystem.inMemory().getAuthService();
        expectError("Sign up with a weak password (< 6 chars) -> rejected", "at least 6",
                () -> auth.signUp("Ali", "ali2@gmail.com", "123", "123", ""));
        expectError("Sign up with mismatched passwords -> rejected", "do not match",
                () -> auth.signUp("Ali", "ali2@gmail.com", "secret1", "secret2", ""));
        expectError("Sign up with an invalid email -> rejected", "valid email",
                () -> auth.signUp("Ali", "ali-at-gmail", "secret1", "secret1", ""));
    }

    /** Admin email pattern is detected. */
    private static void testAdminEmailDetection() {
        run("Admin detection", () -> {
            LibrarySystem s = LibrarySystem.inMemory();
            AuthService auth = s.getAuthService();
            check("Admin email pattern usman.admin@library.com -> ADMIN", Role.ADMIN,
                    auth.detectRole("usman.admin@library.com"));
            auth.signUp("Usman", "usman.admin@library.com", "Admin@123", "Admin@123", AuthService.ADMIN_ACCESS_CODE);
            check("Admin sign in -> ADMIN role", Role.ADMIN, auth.signIn("usman.admin@library.com", "Admin@123").getRole());
        });
    }

    /** Normal emails are students. */
    private static void testStudentEmailDetection() {
        AuthService auth = LibrarySystem.inMemory().getAuthService();
        check("Normal email ali@gmail.com -> STUDENT", Role.STUDENT, auth.detectRole("ali@gmail.com"));
        check("'admin@library.com' (no <name>.admin) -> STUDENT", Role.STUDENT, auth.detectRole("admin@library.com"));
    }

    /** Wrong password is rejected. */
    private static void testWrongPassword() {
        run("Wrong password", () -> {
            AuthService auth = LibrarySystem.inMemory().getAuthService();
            auth.signUp("Ali", "ali@gmail.com", "Student@123", "Student@123", "");
            expectError("Sign in with a wrong password -> rejected", "Invalid email or password",
                    () -> auth.signIn("ali@gmail.com", "wrong-pass"));
        });
    }

    /** Admin sign-up needs the access code. */
    private static void testAdminWrongCode() {
        AuthService auth = LibrarySystem.inMemory().getAuthService();
        expectError("Admin sign up with a wrong access code -> rejected", "Admin Access Code",
                () -> auth.signUp("X", "x.admin@library.com", "secret1", "secret1", "WRONG"));
    }

    // ==================== reservations / history / persistence ====================

    /** Reserve and cancel. */
    private static void testReserveAndCancel() {
        run("Reserve", () -> {
            LibrarySystem s = sample();
            LibraryService lib = s.getLibraryService();
            lib.reserveBook("B2", "MEM-001");
            String afterReserve = lib.findById("B2").getStatus() + " by " + lib.findById("B2").getReservedBy();
            expectError("Another member cannot issue a book reserved by someone else", "reserved by another member",
                    () -> lib.issueBook("B2", "MEM-002", 14));
            expectError("Another member cannot cancel the reservation", "own reservations",
                    () -> lib.cancelReservation("B2", "MEM-002"));
            lib.cancelReservation("B2", "MEM-001");
            check("Reserve + cancel reservation -> Reserved, then Available",
                    "Reserved by MEM-001 -> Available", afterReserve + " -> " + lib.findById("B2").getStatus());
            expectError("Reserve a book that is not Available -> rejected", "cannot be reserved",
                    () -> {
                        lib.issueBook("B3", "MEM-002", 14);
                        lib.reserveBook("B3", "MEM-001");
                    });
        });
    }

    /** Transaction history records issue and return. */
    private static void testTransactionHistory() {
        run("History", () -> {
            LibrarySystem s = sample();
            s.getLibraryService().setActor("usman.admin@library.com");
            Loan loan = s.getLibraryService().issueBook("B1", "MEM-001", 14);
            s.getLibraryService().returnBook(loan.getLoanId());
            List<Transaction> issues = s.getTransactionService().filterByType(TransactionType.ISSUE);
            List<Transaction> returns = s.getTransactionService().filterByType(TransactionType.RETURN);
            Transaction newest = s.getTransactionService().getAll().get(0);
            check("Transaction history records ISSUE and RETURN (newest first, performed by admin)",
                    "1 issue, 1 return, newest=Return by usman.admin@library.com",
                    issues.size() + " issue, " + returns.size() + " return, newest=" + newest.getType()
                            + " by " + newest.getPerformedBy());
        });
    }

    /** Saving and loading through DataStore in a temporary folder. */
    private static void testPersistenceRoundTrip() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("library-test");
            LibrarySystem first = new LibrarySystem(dir);
            first.load(); // seeds sample data on first run
            first.getLibraryService().addBook(new Book("T1", "Title, with comma", "Au \"thor\"", Category.OTHER));
            first.save();
            LibrarySystem second = new LibrarySystem(dir);
            second.load();
            Book t = second.getLibraryService().findById("T1");
            check("Persistence: data saved to CSV files and loaded again (temp folder)",
                    "15 books, T1='Title, with comma' by Au \"thor\", 2 users, 1 overdue",
                    second.getLibraryService().getAllBooks().size() + " books, T1='" + (t == null ? "?" : t.getTitle())
                            + "' by " + (t == null ? "?" : t.getAuthor()) + ", "
                            + second.getAuthService().getAllUsers().size() + " users, "
                            + second.getLibraryService().getOverdueLoans().size() + " overdue");
        } catch (Exception e) {
            check("Persistence round trip", "no exception", e);
        } finally {
            deleteQuietly(dir);
        }
    }

    /**
     * Deletes a temporary folder recursively, ignoring errors.
     *
     * @param dir the folder (may be null)
     */
    private static void deleteQuietly(Path dir) {
        if (dir == null) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                    // best effort
                }
            });
        } catch (IOException ignored) {
            // best effort
        }
    }
}
