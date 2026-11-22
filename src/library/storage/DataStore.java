package library.storage;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence for the library. Users, books, members, loans and transactions
 * are saved as simple CSV files inside a data folder (by default {@code data/}) using only
 * java.nio. Fields containing commas or quotes are quoted CSV-style. Corrupt lines are skipped
 * instead of crashing the application.
 *
 * @author Usman
 */
public class DataStore {

    /** File name for users. */
    public static final String USERS_FILE = "users.csv";
    /** File name for books. */
    public static final String BOOKS_FILE = "books.csv";
    /** File name for members. */
    public static final String MEMBERS_FILE = "members.csv";
    /** File name for loans. */
    public static final String LOANS_FILE = "loans.csv";
    /** File name for transactions. */
    public static final String TRANSACTIONS_FILE = "transactions.csv";

    /** Folder that holds the data files. */
    private final Path dataDir;

    /**
     * Creates a DataStore that reads and writes files in the given folder.
     *
     * @param dataDir the data folder (created on first save if missing)
     */
    public DataStore(Path dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Returns the data folder.
     *
     * @return the data folder path
     */
    public Path getDataDir() {
        return dataDir;
    }

    /**
     * Checks whether saved data exists (the users and books files are present).
     * When it returns false the application seeds sample data.
     *
     * @return true if previously saved data exists
     */
    public boolean hasData() {
        return Files.exists(dataDir.resolve(USERS_FILE)) && Files.exists(dataDir.resolve(BOOKS_FILE));
    }

    // ==================== LOAD ====================

    /**
     * Loads all users.
     *
     * @return list of users (empty if the file is missing)
     * @throws LibraryException if the file cannot be read
     */
    public List<User> loadUsers() throws LibraryException {
        List<User> list = new ArrayList<>();
        for (List<String> f : readRows(USERS_FILE, 5)) {
            try {
                list.add(new User(f.get(0), f.get(1), f.get(2), Role.fromString(f.get(3)), f.get(4)));
            } catch (IllegalArgumentException e) {
                // skip corrupt row
            }
        }
        return list;
    }

    /**
     * Loads all books.
     *
     * @return list of books (empty if the file is missing)
     * @throws LibraryException if the file cannot be read
     */
    public List<Book> loadBooks() throws LibraryException {
        List<Book> list = new ArrayList<>();
        for (List<String> f : readRows(BOOKS_FILE, 8)) {
            try {
                list.add(new Book(f.get(0), f.get(1), f.get(2), Category.fromString(f.get(3)),
                        BookStatus.fromString(f.get(4)), f.get(5), f.get(6), f.get(7)));
            } catch (IllegalArgumentException e) {
                // skip corrupt row
            }
        }
        return list;
    }

    /**
     * Loads all members.
     *
     * @return list of members (empty if the file is missing)
     * @throws LibraryException if the file cannot be read
     */
    public List<Member> loadMembers() throws LibraryException {
        List<Member> list = new ArrayList<>();
        for (List<String> f : readRows(MEMBERS_FILE, 4)) {
            list.add(new Member(f.get(0), f.get(1), f.get(2), f.get(3)));
        }
        return list;
    }

    /**
     * Loads all loans.
     *
     * @return list of loans (empty if the file is missing)
     * @throws LibraryException if the file cannot be read
     */
    public List<Loan> loadLoans() throws LibraryException {
        List<Loan> list = new ArrayList<>();
        for (List<String> f : readRows(LOANS_FILE, 7)) {
            list.add(new Loan(f.get(0), f.get(1), f.get(2), f.get(3), f.get(4), f.get(5),
                    Boolean.parseBoolean(f.get(6))));
        }
        return list;
    }

    /**
     * Loads all transactions (oldest first).
     *
     * @return list of transactions (empty if the file is missing)
     * @throws LibraryException if the file cannot be read
     */
    public List<Transaction> loadTransactions() throws LibraryException {
        List<Transaction> list = new ArrayList<>();
        for (List<String> f : readRows(TRANSACTIONS_FILE, 6)) {
            try {
                list.add(new Transaction(f.get(0), TransactionType.fromString(f.get(1)),
                        f.get(2), f.get(3), f.get(4), f.get(5)));
            } catch (IllegalArgumentException e) {
                // skip corrupt row
            }
        }
        return list;
    }

    // ==================== SAVE ====================

    /**
     * Saves all users.
     *
     * @param users the users to save
     * @throws LibraryException if writing fails
     */
    public void saveUsers(List<User> users) throws LibraryException {
        List<String> lines = new ArrayList<>();
        lines.add("fullName,email,passwordHash,role,memberId");
        for (User u : users) {
            lines.add(toCsv(u.getFullName(), u.getEmail(), u.getPasswordHash(), u.getRole().name(), u.getMemberId()));
        }
        writeLines(USERS_FILE, lines);
    }

    /**
     * Saves all books.
     *
     * @param books the books to save
     * @throws LibraryException if writing fails
     */
    public void saveBooks(List<Book> books) throws LibraryException {
        List<String> lines = new ArrayList<>();
        lines.add("id,title,author,category,status,issuedTo,dueDate,reservedBy");
        for (Book b : books) {
            lines.add(toCsv(b.getId(), b.getTitle(), b.getAuthor(), b.getCategory().name(),
                    b.getStatus().name(), b.getIssuedTo(), b.getDueDate(), b.getReservedBy()));
        }
        writeLines(BOOKS_FILE, lines);
    }

    /**
     * Saves all members.
     *
     * @param members the members to save
     * @throws LibraryException if writing fails
     */
    public void saveMembers(List<Member> members) throws LibraryException {
        List<String> lines = new ArrayList<>();
        lines.add("memberId,name,phone,email");
        for (Member m : members) {
            lines.add(toCsv(m.getMemberId(), m.getName(), m.getPhone(), m.getEmail()));
        }
        writeLines(MEMBERS_FILE, lines);
    }

    /**
     * Saves all loans.
     *
     * @param loans the loans to save
     * @throws LibraryException if writing fails
     */
    public void saveLoans(List<Loan> loans) throws LibraryException {
        List<String> lines = new ArrayList<>();
        lines.add("loanId,bookId,memberId,issueDate,dueDate,returnDate,active");
        for (Loan l : loans) {
            lines.add(toCsv(l.getLoanId(), l.getBookId(), l.getMemberId(), l.getIssueDate(),
                    l.getDueDate(), l.getReturnDate(), String.valueOf(l.isActive())));
        }
        writeLines(LOANS_FILE, lines);
    }

    /**
     * Saves all transactions (oldest first).
     *
     * @param transactions the transactions to save
     * @throws LibraryException if writing fails
     */
    public void saveTransactions(List<Transaction> transactions) throws LibraryException {
        List<String> lines = new ArrayList<>();
        lines.add("dateTime,type,bookId,memberId,performedBy,details");
        for (Transaction t : transactions) {
            lines.add(toCsv(t.getDateTime(), t.getType().name(), t.getBookId(), t.getMemberId(),
                    t.getPerformedBy(), t.getDetails()));
        }
        writeLines(TRANSACTIONS_FILE, lines);
    }

    // ==================== CSV HELPERS ====================

    /**
     * Reads a CSV file, skipping the header line and rows with too few fields.
     *
     * @param fileName  file name inside the data folder
     * @param minFields minimum number of fields a valid row has
     * @return parsed rows
     * @throws LibraryException if the file exists but cannot be read
     */
    private List<List<String>> readRows(String fileName, int minFields) throws LibraryException {
        List<List<String>> rows = new ArrayList<>();
        Path file = dataDir.resolve(fileName);
        if (!Files.exists(file)) return rows;
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= minFields) rows.add(fields);
            }
        } catch (IOException e) {
            throw new LibraryException("Could not read " + file + ": " + e.getMessage(), e);
        }
        return rows;
    }

    /**
     * Writes lines to a file atomically (write to a temp file, then move).
     *
     * @param fileName file name inside the data folder
     * @param lines    the lines to write
     * @throws LibraryException if writing fails
     */
    private void writeLines(String fileName, List<String> lines) throws LibraryException {
        try {
            Files.createDirectories(dataDir);
            Path target = dataDir.resolve(fileName);
            Path tmp = dataDir.resolve(fileName + ".tmp");
            Files.write(tmp, lines, StandardCharsets.UTF_8);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new LibraryException("Could not save " + fileName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Joins values into one CSV line, quoting values that contain commas or quotes.
     * Line breaks inside values are replaced with spaces.
     *
     * @param values the values
     * @return the CSV line
     */
    static String toCsv(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            String v = values[i] == null ? "" : values[i].replace('\r', ' ').replace('\n', ' ');
            if (v.contains(",") || v.contains("\"")) {
                sb.append('"').append(v.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(v);
            }
        }
        return sb.toString();
    }

    /**
     * Parses one CSV line, honouring double-quoted fields and escaped quotes.
     *
     * @param line the CSV line
     * @return the field values
     */
    static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields;
    }
}
