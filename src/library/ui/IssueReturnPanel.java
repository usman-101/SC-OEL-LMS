package library.ui;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BookStatus;
import library.model.Loan;
import library.model.Member;
import library.service.LibraryService;
import library.service.MemberService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Issue and return screen. The top card issues a book (Book ID, Member ID, loan period in days,
 * default 14) and the table lists active loans; selecting a loan and clicking Return records the
 * return date and makes the book Available again. Overdue loans are highlighted in red.
 *
 * @author Usman
 */
public class IssueReturnPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Separator between ID and description in combo items. */
    private static final String SEP = "  -  ";

    /** Owning window. */
    private final MainFrame frame;
    /** Issuable books. */
    private final JComboBox<String> bookCombo = UIStyle.createComboBox(new String[0]);
    /** Members. */
    private final JComboBox<String> memberCombo = UIStyle.createComboBox(new String[0]);
    /** Loan period in days. */
    private final JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(
            LibraryService.DEFAULT_LOAN_DAYS, 1, LibraryService.MAX_LOAN_DAYS, 1));
    /** Active loans table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Book ID", "Title", "Member", "Issue Date", "Due Date", "Status"});
    /** Loans shown in the table, by model row. */
    private final transient List<Loan> rows = new ArrayList<>();
    /** Active loans table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\u2705");
    /** Count of active loans. */
    private final JLabel countLabel = UIStyle.label("", UIStyle.SMALL_BOLD, UIStyle.MUTED);

    /**
     * Creates the panel.
     *
     * @param frame the main window
     */
    public IssueReturnPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setOpaque(false);

        JPanel north = new JPanel(new BorderLayout(0, 4));
        north.setOpaque(false);
        north.add(UIStyle.pageHeader("Issue / Return",
                "Issue Available books (or books reserved by the same member) and record returns."), BorderLayout.NORTH);
        north.add(buildIssueCard(), BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        UIStyle.styleTable(table, r -> r < rows.size() && frame.getSystem().getLibraryService().isOverdue(rows.get(r)));
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setMinWidth(95);
        table.getColumnModel().getColumn(4).setMinWidth(95);

        UIStyle.RoundedPanel loansCard = new UIStyle.RoundedPanel(new BorderLayout(0, 10));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UIStyle.label("Active Loans", UIStyle.HEADER, UIStyle.TEXT), BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(countLabel);
        JButton returnButton = UIStyle.createButton("Return Selected Book", UIStyle.SUCCESS);
        returnButton.addActionListener(e -> doReturn());
        actions.add(returnButton);
        head.add(actions, BorderLayout.EAST);
        loansCard.add(head, BorderLayout.NORTH);
        loansCard.add(tableCard, BorderLayout.CENTER);
        add(loansCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Builds the issue form card.
     *
     * @return the card
     */
    private JPanel buildIssueCard() {
        UIStyle.RoundedPanel card = new UIStyle.RoundedPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 4, 12);
        c.gridx = 0;
        card.add(UIStyle.formLabel("Book (Available / Reserved)"), c);
        c.gridx = 1;
        card.add(UIStyle.formLabel("Member"), c);
        c.gridx = 2;
        card.add(UIStyle.formLabel("Loan period (days)"), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 0.55;
        card.add(UIStyle.height(bookCombo, 34), c);
        c.gridx = 1;
        c.weightx = 0.45;
        card.add(UIStyle.height(memberCombo, 34), c);
        c.gridx = 2;
        c.weightx = 0;
        UIStyle.styleSpinner(daysSpinner);
        card.add(UIStyle.height(daysSpinner, 34), c);
        c.gridx = 3;
        c.insets = new Insets(0, 0, 4, 0);
        JButton issue = UIStyle.primaryButton("Issue Book");
        issue.addActionListener(e -> doIssue());
        card.add(issue, c);
        return card;
    }

    /**
     * Extracts the ID from a combo item such as "B001  -  Clean Code".
     *
     * @param item the combo item
     * @return the ID, or empty string
     */
    private static String idOf(Object item) {
        if (item == null) return "";
        String s = item.toString();
        int i = s.indexOf(SEP);
        return i < 0 ? s.trim() : s.substring(0, i).trim();
    }

    /**
     * Issues the selected book to the selected member.
     */
    private void doIssue() {
        try {
            int days = ((Number) daysSpinner.getValue()).intValue();
            Loan loan = frame.getSystem().getLibraryService().issueBook(idOf(bookCombo.getSelectedItem()),
                    idOf(memberCombo.getSelectedItem()), days);
            frame.dataChanged();
            Book b = frame.getSystem().getLibraryService().findById(loan.getBookId());
            String msg = "Issued '" + (b != null ? b.getTitle() : loan.getBookId()) + "' to " + loan.getMemberId()
                    + ".\nIssue date: " + loan.getIssueDate() + "\nDue date: " + loan.getDueDate();
            UIStyle.showInfo(this, msg);
            frame.setStatus("Book " + loan.getBookId() + " issued to " + loan.getMemberId() + ", due " + loan.getDueDate() + ".");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Returns the book of the selected active loan.
     */
    private void doReturn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIStyle.showError(this, "Please select an active loan in the table first.");
            return;
        }
        Loan loan = rows.get(table.convertRowIndexToModel(row));
        try {
            LibraryService lib = frame.getSystem().getLibraryService();
            long late = lib.getDaysOverdue(loan);
            lib.returnBook(loan.getLoanId());
            frame.dataChanged();
            String msg = "Book " + loan.getBookId() + " returned on " + loan.getReturnDate()
                    + ". Status is now Available." + (late > 0 ? "\nIt was returned " + late + " day(s) late." : "");
            UIStyle.showInfo(this, msg);
            frame.setStatus("Book " + loan.getBookId() + " returned.");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Reloads the combos and active loans table.
     */
    @Override
    public void refresh() {
        LibraryService lib = frame.getSystem().getLibraryService();
        MemberService members = frame.getSystem().getMemberService();

        String selectedBook = idOf(bookCombo.getSelectedItem());
        bookCombo.removeAllItems();
        for (Book b : lib.getAllBooks()) {
            if (b.getStatus() == BookStatus.AVAILABLE) {
                bookCombo.addItem(b.getId() + SEP + b.getTitle());
            } else if (b.getStatus() == BookStatus.RESERVED && !lib.hasActiveLoan(b.getId())) {
                bookCombo.addItem(b.getId() + SEP + b.getTitle() + "  [Reserved by "
                        + (b.getReservedBy().isEmpty() ? "library" : b.getReservedBy()) + "]");
            }
        }
        reselect(bookCombo, selectedBook);

        String selectedMember = idOf(memberCombo.getSelectedItem());
        memberCombo.removeAllItems();
        for (Member m : members.getAllMembers()) {
            memberCombo.addItem(m.getMemberId() + SEP + m.getName());
        }
        reselect(memberCombo, selectedMember);

        rows.clear();
        model.setRowCount(0);
        for (Loan l : lib.getActiveLoans()) {
            rows.add(l);
            Book b = lib.findById(l.getBookId());
            long late = lib.getDaysOverdue(l);
            String status = lib.isOverdue(l) ? "Overdue by " + late + " day(s)" : "On loan";
            model.addRow(new Object[]{l.getBookId(), b != null ? b.getTitle() : "(deleted)",
                    BooksPanel.memberLabel(l.getMemberId(), members), l.getIssueDate(), l.getDueDate(), status});
        }
        countLabel.setText(rows.size() + " active loan(s)");
        if (rows.isEmpty()) {
            tableCard.showEmpty("No active loans. All books are on the shelf.");
        } else {
            tableCard.showTable();
        }
    }

    /**
     * Re-selects a combo item by ID after reloading.
     *
     * @param combo the combo
     * @param id    the ID to select
     */
    private static void reselect(JComboBox<String> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (idOf(combo.getItemAt(i)).equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}
