# Java-Based Library Book Management System

A Java Swing desktop application for managing a library, built for the Open-Ended Lab.
It has Sign In / Sign Up with role-based panels (**Admin/Librarian** and **Student**),
full book management, issuing, returning, reservations, overdue tracking, member management
and a persistent transaction history.

- Plain Java (JDK **17 or newer**). No Maven, no Gradle, no external libraries.
- Data is saved as CSV files in a `data/` folder, so it persists between runs.
- All business logic and validation live in the service classes, not in the GUI.

**Author:** Usman

---

## Default login credentials

On the first run (when `data/` does not exist yet) the app seeds sample data automatically.

| Role            | Email                     | Password      |
|-----------------|---------------------------|---------------|
| Admin/Librarian | `usman.admin@library.com` | `Admin@123`   |
| Student         | `ali@gmail.com`           | `Student@123` |

- **Admin access code** (needed to sign up a new admin): `LIB-ADMIN-2026`
- **Sample data:** 14 books in 6 categories, 2 members (MEM-001 Ali Khan, MEM-002 Sara Ahmed),
  1 overdue loan (B004, issued to Ali 30 days ago for 14 days) and 1 normal loan (B001, issued to Sara).
- To reset all data, close the app and delete the `data/` folder.

### Role rule

The role is decided by the email address:

- `<name>.admin@<domain>` (regex `^[A-Za-z0-9_-]+\.admin@[A-Za-z0-9-]+(\.[A-Za-z]{2,})+$`),
  e.g. `usman.admin@library.com`, opens the **Admin Panel**. Signing up with such an email
  shows an extra **Admin Access Code** field.
- Any other valid email (e.g. `ali@gmail.com`) is a **Student**. A student sign-up automatically
  creates a library member record (MEM-xxx).

---

## Compile and run

Run these commands from the project root folder (the folder that contains `src`).

### Windows (Command Prompt)
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out library.Main
```

### Linux / macOS
```
javac -d out $(find src -name "*.java")
java -cp out library.Main
```

> If `java` / `javac` are "not recognized", the JDK is not on your PATH. Either use the
> full path, e.g. `"C:\Program Files\Eclipse Adoptium\jdk-17\bin\javac"`, or add the JDK's
> `bin` folder to the PATH environment variable (and set `JAVA_HOME`).
> To build class files that also run on Java 17 while using a newer JDK, add `--release 17`
> to the `javac` command.

### Run the tests
```
dir /s /b src\*.java test\*.java > sources.txt
javac -d out @sources.txt
java -cp out library.LibraryServiceTest
```
(Linux/macOS: `javac -d out $(find src test -name "*.java") && java -cp out library.LibraryServiceTest`)

The tests use in-memory data and a temporary folder only. They never touch `data/`.
The latest output is in [`docs/test_results.txt`](docs/test_results.txt) (48 checks, all passing).

### Generate Javadoc
```
javadoc -d docs/javadoc -sourcepath src -subpackages library -author -private
```
Open `docs/javadoc/index.html` in a browser.

### Opening in an IDE
- **IntelliJ IDEA:** File > Open > select the project folder. Mark `src` as Sources Root
  (and `test` as Test Sources Root) if it is not detected, set the Project SDK to JDK 17+,
  then run `library.Main`.
- **Eclipse:** File > Import > General > Existing Projects into Workspace > select the folder.
  The included `.project` / `.classpath` files configure `src` and `test` as source folders (JavaSE-17).
- **NetBeans:** File > New Project > Java with Ant > Java Project with Existing Sources,
  then add `src` (and `test`) as source package folders. Main class: `library.Main`.

---

## Features

### Authentication
- Sign In and Sign Up screens (switch with "Don't have an account? Sign Up").
- Validation: valid email format, password at least 6 characters, passwords must match,
  no duplicate email, no empty fields, admin code required for admin emails.
- Passwords are stored only as **SHA-256 hashes** (`java.security.MessageDigest`).
- Wrong credentials show a clear error dialog. Logout returns to Sign In.

### Admin / Librarian panel
| Page | What it does |
|------|--------------|
| Dashboard | Stat cards: Total, Available, Issued and Reserved books, Total Members, Overdue count, plus recent activity. Refreshes after every change. |
| Books | Add / Update / Delete (with confirmation) / Set Status / Clear. Clicking a row fills the form. Live search (Book ID / Title / Author, case-insensitive partial match) with Status and Category filters and Reset. Shows "The library is empty. No books to display." when there are no books. Issued books cannot be deleted. |
| Issue/Return | Issue a book to a member for N days (default 14). Only Available books, or books reserved by the same member, can be issued. Return a selected active loan: the return date is recorded and the status goes back to Available. |
| Members | Register members (auto IDs MEM-001...), update, delete (blocked while they have active loans) and search by name or ID. |
| Overdue | Active loans past their due date with days overdue, highlighted in red. Shows "No overdue books" when there are none. |
| Transactions | Persistent log, newest first: ADD, UPDATE, DELETE, STATUS_CHANGE, ISSUE, RETURN, RESERVE, CANCEL_RESERVATION, with a filter by type. |

### Student panel (read-only catalogue)
- **Browse Books:** same table, search and filters. **Reserve Book** (Available books only) and
  **Cancel Reservation** (own reservations only).
- **My Books:** issued and reserved books with issue and due dates. Late books are shown in red.
- **My History:** the student's own transactions.
- Top bar: "Welcome, name" with counts of issued, reserved and overdue books.

### UI: "liquid glass" theme (black and blue)
- **Background:** near-black (`#04060C`) lit by soft blue and cyan glows (`GlassBackground`).
- **Panels:** frosted, translucent glass cards with rounded corners, a light gradient rim and
  a top sheen, so the blue glow shows through them.
- **Buttons and highlights:** glossy liquid pills in royal blue `#2563EB`, sky `#0EA5E9` and
  indigo `#6366F1`. Red `#EF4444` is used only for Delete and overdue items. Buttons get a soft
  glow on hover. The selected sidebar item is a blue liquid pill.
- **Inputs:** dark rounded text fields, combo boxes and spinners with a blue focus ring and blue
  chevrons. Scroll bars are thin glass thumbs.
- **Tables:** dark alternating rows, a navy header with light-blue bold text and a glowing
  accent line, blue selection, sorting, row height 28.
- **Dialogs:** dark `JOptionPane` dialogs with blue buttons. The app uses the cross-platform
  (Metal) look and feel, styled by `UIStyle.installTheme()`, so it looks the same on every OS.
- **Window:** at least 1100x700 and resizable (the main window opens maximized). A status bar
  at the bottom shows feedback. Fonts are Segoe UI, falling back to SansSerif.

---

## Project structure

```
SC-OEL/
|-- src/library/
|   |-- Main.java                 entry point (Swing event thread, look and feel)
|   |-- model/                    Book, BookStatus, Category, Member, User, Role,
|   |                             Loan, Transaction, TransactionType
|   |-- exception/                LibraryException (checked, used for all validation errors)
|   |-- service/                  LibraryService, MemberService, AuthService,
|   |                             TransactionService, LibrarySystem (wiring + sample data)
|   |-- storage/                  DataStore (CSV files in data/)
|   `-- ui/                       LoginFrame, SignUpPanel, MainFrame, AdminPanel, StudentPanel,
|                                 DashboardPanel, BooksPanel, IssueReturnPanel, MembersPanel,
|                                 OverduePanel, TransactionsPanel, BrowseBooksPanel, MyBooksPanel,
|                                 MyHistoryPanel, NavigationPanel, SidebarPanel, SearchFilterBar,
|                                 TableCard, StatCard, GlassBackground, UIStyle, Refreshable
|-- test/library/LibraryServiceTest.java   plain-Java tests (PASS/FAIL + summary)
|-- docs/
|   |-- javadoc/                  generated HTML documentation
|   |-- test_results.txt          latest test output
|   `-- git_evidence.txt          git status / log / diff --stat evidence
|-- data/                         runtime data (created automatically, git-ignored)
`-- README.md
```
