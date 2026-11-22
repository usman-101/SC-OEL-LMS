package library.ui;

import library.model.Transaction;
import library.model.TransactionType;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Transaction history: the persistent log of all operations (newest first) with a filter by
 * transaction type. Columns: Date/Time, Type, Book ID, Member ID, Performed By, Details.
 *
 * @author Usman
 */
public class TransactionsPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Filter entry for all types. */
    private static final String ALL_TYPES = "All types";

    /** Owning window. */
    private final MainFrame frame;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Date/Time", "Type", "Book ID", "Member ID", "Performed By", "Details"});
    /** History table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDCDC");
    /** Type filter. */
    private final JComboBox<String> typeFilter;
    /** Count label. */
    private final JLabel countLabel = UIStyle.label(" ", UIStyle.SMALL_BOLD, UIStyle.MUTED);

    /**
     * Creates the transactions panel.
     *
     * @param frame the main window
     */
    public TransactionsPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setOpaque(false);

        String[] types = new String[TransactionType.values().length + 1];
        types[0] = ALL_TYPES;
        for (int i = 0; i < TransactionType.values().length; i++) {
            types[i + 1] = TransactionType.values()[i].getDisplayName();
        }
        typeFilter = UIStyle.createComboBox(types);
        typeFilter.addActionListener(e -> refresh());

        JPanel north = new JPanel(new BorderLayout(0, 4));
        north.setOpaque(false);
        north.add(UIStyle.pageHeader("Transaction History", "Every change to books, loans and reservations, newest first."),
                BorderLayout.NORTH);
        UIStyle.RoundedPanel bar = new UIStyle.RoundedPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        bar.add(UIStyle.formLabel("Filter by type"));
        bar.add(UIStyle.height(typeFilter, 32));
        bar.add(countLabel);
        north.add(bar, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        UIStyle.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(190);
        table.getColumnModel().getColumn(5).setPreferredWidth(340);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Reloads the history using the current type filter.
     */
    @Override
    public void refresh() {
        String sel = (String) typeFilter.getSelectedItem();
        TransactionType type = (sel == null || ALL_TYPES.equals(sel)) ? null : TransactionType.fromString(sel);
        List<Transaction> list = frame.getSystem().getTransactionService().filterByType(type);
        model.setRowCount(0);
        for (Transaction t : list) {
            model.addRow(new Object[]{t.getDateTime(), t.getType().getDisplayName(), t.getBookId(), t.getMemberId(),
                    t.getPerformedBy(), t.getDetails()});
        }
        countLabel.setText(list.size() + " transaction(s)");
        if (list.isEmpty()) {
            tableCard.showEmpty("No transactions" + (type == null ? " recorded yet." : " of type " + sel + "."));
        } else {
            tableCard.showTable();
        }
    }
}
