package library.ui;

import library.model.Transaction;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Student "My History" page: the student's own transactions (issues, returns, reservations
 * and cancellations), newest first.
 *
 * @author Usman
 */
public class MyHistoryPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    private final MainFrame frame;
    /** The student panel (for the member ID). */
    private final StudentPanel student;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Date/Time", "Type", "Book ID", "Details"});
    /** Table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDCDC");

    /**
     * Creates the page.
     *
     * @param frame   the main window
     * @param student the student panel
     */
    public MyHistoryPanel(MainFrame frame, StudentPanel student) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        this.student = student;
        setOpaque(false);
        add(UIStyle.pageHeader("My History", "Your own library transactions, newest first."), BorderLayout.NORTH);
        UIStyle.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(420);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Reloads the student's transactions.
     */
    @Override
    public void refresh() {
        List<Transaction> list = frame.getSystem().getTransactionService()
                .getForMember(student.memberIdOrEmpty(), frame.getUser().getEmail());
        model.setRowCount(0);
        for (Transaction t : list) {
            model.addRow(new Object[]{t.getDateTime(), t.getType().getDisplayName(), t.getBookId(), t.getDetails()});
        }
        if (list.isEmpty()) {
            tableCard.showEmpty("You have no transactions yet.");
        } else {
            tableCard.showTable();
        }
    }
}
