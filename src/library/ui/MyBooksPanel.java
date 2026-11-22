package library.ui;

import library.model.Book;
import library.model.Loan;
import library.service.LibraryService;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Student "My Books" page: the student's currently issued and reserved books with issue date,
 * due date and an overdue warning (rows highlighted in red) when a book is late.
 *
 * @author Usman
 */
public class MyBooksPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    private final MainFrame frame;
    /** The student panel (for the member ID). */
    private final StudentPanel student;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Book ID", "Title", "Author", "Type", "Issue Date", "Due Date", "Status"});
    /** Whether each model row is overdue. */
    private final transient List<Boolean> overdueRows = new ArrayList<>();
    /** Table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDCDA");

    /**
     * Creates the page.
     *
     * @param frame   the main window
     * @param student the student panel
     */
    public MyBooksPanel(MainFrame frame, StudentPanel student) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        this.student = student;
        setOpaque(false);
        add(UIStyle.pageHeader("My Books", "Books currently issued to you or reserved by you. Late books are shown in red."),
                BorderLayout.NORTH);
        UIStyle.styleTable(table, r -> r < overdueRows.size() && overdueRows.get(r));
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setMinWidth(95);
        table.getColumnModel().getColumn(5).setMinWidth(95);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Reloads the student's issued and reserved books.
     */
    @Override
    public void refresh() {
        LibraryService lib = frame.getSystem().getLibraryService();
        String mid = student.memberIdOrEmpty();
        model.setRowCount(0);
        overdueRows.clear();
        for (Loan l : lib.getActiveLoansByMember(mid)) {
            Book b = lib.findById(l.getBookId());
            boolean late = lib.isOverdue(l);
            String status;
            if (late) {
                status = "OVERDUE by " + lib.getDaysOverdue(l) + " day(s) - please return";
            } else {
                long left = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(l.getDueDate(), LibraryService.DATE_FMT));
                status = left == 0 ? "Due today" : left + " day(s) left";
            }
            model.addRow(new Object[]{l.getBookId(), b != null ? b.getTitle() : "(deleted)",
                    b != null ? b.getAuthor() : "", "Issued", l.getIssueDate(), l.getDueDate(), status});
            overdueRows.add(late);
        }
        for (Book b : lib.getReservedBooksByMember(mid)) {
            model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), "Reserved", "-", "-",
                    "Reserved - collect from the desk"});
            overdueRows.add(false);
        }
        if (model.getRowCount() == 0) {
            tableCard.showEmpty("You have no issued or reserved books.");
        } else {
            tableCard.showTable();
        }
    }
}
