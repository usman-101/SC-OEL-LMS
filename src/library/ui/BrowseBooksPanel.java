package library.ui;

import library.exception.LibraryException;
import library.model.Book;
import library.service.LibraryService;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Student "Browse Books" page: read-only catalogue with the same search and filter bar as the
 * admin view. Students can reserve an Available book or cancel their own reservation.
 *
 * @author Usman
 */
public class BrowseBooksPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    private final MainFrame frame;
    /** The student panel (for the member ID). */
    private final StudentPanel student;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(BooksPanel.COLUMNS);
    /** Books table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDCED");
    /** Search and filter bar. */
    private final SearchFilterBar filterBar = new SearchFilterBar();

    /**
     * Creates the browse page.
     *
     * @param frame   the main window
     * @param student the student panel
     */
    public BrowseBooksPanel(MainFrame frame, StudentPanel student) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        this.student = student;
        setOpaque(false);

        JPanel north = new JPanel(new BorderLayout(0, 4));
        north.setOpaque(false);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UIStyle.pageHeader("Browse Books", "Search the catalogue and reserve Available books."),
                BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setOpaque(false);
        JButton reserve = UIStyle.createButton("Reserve Book", UIStyle.SUCCESS);
        JButton cancel = UIStyle.createButton("Cancel Reservation", UIStyle.DANGER);
        reserve.addActionListener(e -> doReserve());
        cancel.addActionListener(e -> doCancel());
        actions.add(reserve);
        actions.add(cancel);
        head.add(actions, BorderLayout.EAST);
        north.add(head, BorderLayout.NORTH);
        north.add(filterBar, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        UIStyle.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(52);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        filterBar.setOnChange(this::refresh);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Returns the ID of the selected book.
     *
     * @return the book ID
     * @throws LibraryException if no row is selected
     */
    private String selectedBookId() throws LibraryException {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new LibraryException("Please select a book in the table first.");
        }
        return (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
    }

    /**
     * Reserves the selected book for the logged-in student.
     */
    private void doReserve() {
        try {
            Book b = frame.getSystem().getLibraryService().reserveBook(selectedBookId(), student.requireMemberId());
            frame.dataChanged();
            UIStyle.showInfo(this, "'" + b.getTitle() + "' is now reserved for you.\nCollect it from the library desk.");
            frame.setStatus("Reserved " + b.getId() + ".");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Cancels the student's reservation of the selected book.
     */
    private void doCancel() {
        try {
            String id = selectedBookId();
            if (!UIStyle.confirm(this, "Cancel your reservation of book " + id + "?", "Cancel Reservation")) return;
            Book b = frame.getSystem().getLibraryService().cancelReservation(id, student.requireMemberId());
            frame.dataChanged();
            frame.setStatus("Reservation of " + b.getId() + " cancelled. The book is Available again.");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Reloads the catalogue using the current search and filters.
     */
    @Override
    public void refresh() {
        LibraryService lib = frame.getSystem().getLibraryService();
        model.setRowCount(0);
        if (lib.isEmpty()) {
            tableCard.showEmpty(BooksPanel.EMPTY_MESSAGE);
            return;
        }
        List<Book> books = filterBar.apply(lib);
        for (Book b : books) {
            model.addRow(BooksPanel.toRow(b, frame.getSystem().getMemberService()));
        }
        if (books.isEmpty()) {
            tableCard.showEmpty("No books match your search or filters.");
        } else {
            tableCard.showTable();
        }
    }
}
