package library.ui;

import library.model.Book;
import library.model.Loan;
import library.service.LibraryService;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Overdue tracking: lists every loan that has not been returned and whose due date is
 * before today, with the number of days overdue. Rows are highlighted in red; when there
 * are none a "No overdue books" message is shown.
 *
 * @author Usman
 */
public class OverduePanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Message shown when nothing is overdue. */
    public static final String EMPTY_MESSAGE = "No overdue books. All loans are on time.";

    /** Owning window. */
    private final MainFrame frame;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Book ID", "Title", "Member", "Due Date", "Days Overdue"});
    /** Overdue table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83C\uDF89");
    /** Summary line. */
    private final JLabel summary = UIStyle.label(" ", UIStyle.BODY_BOLD, UIStyle.DANGER);

    /**
     * Creates the overdue panel.
     *
     * @param frame the main window
     */
    public OverduePanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setOpaque(false);
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(UIStyle.pageHeader("Overdue Books", "Loans not returned whose due date is before today."),
                BorderLayout.WEST);
        north.add(summary, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        UIStyle.styleTable(table, r -> true);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Reloads the overdue loans.
     */
    @Override
    public void refresh() {
        LibraryService lib = frame.getSystem().getLibraryService();
        List<Loan> overdue = lib.getOverdueLoans();
        model.setRowCount(0);
        for (Loan l : overdue) {
            Book b = lib.findById(l.getBookId());
            model.addRow(new Object[]{l.getBookId(), b != null ? b.getTitle() : "(deleted)",
                    BooksPanel.memberLabel(l.getMemberId(), frame.getSystem().getMemberService()),
                    l.getDueDate(), lib.getDaysOverdue(l)});
        }
        if (overdue.isEmpty()) {
            summary.setText(" ");
            tableCard.showEmpty(EMPTY_MESSAGE);
        } else {
            summary.setText(overdue.size() + " overdue loan(s)");
            tableCard.showTable();
        }
    }
}
