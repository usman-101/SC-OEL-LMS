package library.ui;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BookStatus;
import library.model.Category;
import library.model.Member;
import library.service.LibraryService;
import library.service.MemberService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

/**
 * Admin book management: add, view, update, delete and change status of books,
 * with a live search and filter bar above the table. Clicking a row fills the form.
 * All validation is delegated to {@link LibraryService}; errors are shown in dialogs.
 *
 * @author Usman
 */
public class BooksPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Message shown when the library has no books. */
    public static final String EMPTY_MESSAGE = "The library is empty. No books to display.";
    /** Table column names. */
    public static final String[] COLUMNS = {"ID", "Title", "Author", "Category", "Status", "Issued To / Due Date"};

    /** Owning window. */
    private final MainFrame frame;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(COLUMNS);
    /** Books table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDCED");
    /** Search and filter bar. */
    private final SearchFilterBar filterBar = new SearchFilterBar();
    /** Book ID input. */
    private final JTextField idField = UIStyle.createTextField(14);
    /** Title input. */
    private final JTextField titleField = UIStyle.createTextField(14);
    /** Author input. */
    private final JTextField authorField = UIStyle.createTextField(14);
    /** Category selector. */
    private final JComboBox<Category> categoryCombo = UIStyle.createComboBox(Category.values());
    /** Status selector. */
    private final JComboBox<BookStatus> statusCombo = UIStyle.createComboBox(BookStatus.values());

    /**
     * Creates the books panel.
     *
     * @param frame the main window
     */
    public BooksPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setOpaque(false);

        JPanel north = new JPanel(new BorderLayout(0, 4));
        north.setOpaque(false);
        north.add(UIStyle.pageHeader("Books", "Add, update, delete and change the status of books."), BorderLayout.NORTH);
        north.add(filterBar, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        UIStyle.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(52);
        table.getColumnModel().getColumn(4).setMinWidth(72);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(190);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });
        filterBar.setOnChange(this::reloadTable);

        JPanel center = new JPanel(new BorderLayout(14, 0));
        center.setOpaque(false);
        center.add(tableCard, BorderLayout.CENTER);
        center.add(buildForm(), BorderLayout.EAST);
        add(center, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Builds the book form with action buttons.
     *
     * @return the form card
     */
    private JPanel buildForm() {
        UIStyle.RoundedPanel card = new UIStyle.RoundedPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(290, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(2, 0, 2, 0);

        JButton add = UIStyle.createButton("Add", UIStyle.SUCCESS);
        JButton update = UIStyle.createButton("Update", UIStyle.ACCENT);
        JButton delete = UIStyle.createButton("Delete", UIStyle.DANGER);
        JButton status = UIStyle.createButton("Set Status", UIStyle.WARNING);
        JButton clear = UIStyle.createButton("Clear", UIStyle.NEUTRAL);
        clear.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        add.addActionListener(e -> doAdd());
        update.addActionListener(e -> doUpdate());
        delete.addActionListener(e -> doDelete());
        status.addActionListener(e -> doSetStatus());
        clear.addActionListener(e -> clearForm());

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UIStyle.label("Book Details", UIStyle.HEADER, UIStyle.TEXT), BorderLayout.WEST);
        head.add(clear, BorderLayout.EAST);
        c.gridy = 0;
        card.add(head, c);
        addField(card, c, "Book ID", idField);
        addField(card, c, "Title", titleField);
        addField(card, c, "Author", authorField);
        addField(card, c, "Category", categoryCombo);
        addField(card, c, "Status", statusCombo);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
        buttons.setOpaque(false);
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(status);
        c.gridy++;
        c.insets = new Insets(12, 0, 0, 0);
        card.add(buttons, c);
        c.gridy++;
        c.weighty = 1;
        card.add(new JPanel() {
            private static final long serialVersionUID = 1L;
            {
                setOpaque(false);
            }
        }, c);
        return card;
    }

    /**
     * Adds a labelled input to a GridBag form.
     *
     * @param card  the form panel
     * @param c     the constraints (gridy is advanced)
     * @param label the label text
     * @param input the input component
     */
    static void addField(JPanel card, GridBagConstraints c, String label, javax.swing.JComponent input) {
        c.gridy++;
        c.insets = new Insets(5, 0, 2, 0);
        card.add(UIStyle.formLabel(label), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        UIStyle.height(input, 30);
        card.add(input, c);
    }

    /**
     * Reloads the table using the current search and filter criteria.
     */
    private void reloadTable() {
        LibraryService lib = frame.getSystem().getLibraryService();
        if (lib.isEmpty()) {
            model.setRowCount(0);
            tableCard.showEmpty(EMPTY_MESSAGE);
            return;
        }
        List<Book> books = filterBar.apply(lib);
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(toRow(b, frame.getSystem().getMemberService()));
        }
        if (books.isEmpty()) {
            tableCard.showEmpty("No books match your search or filters.");
        } else {
            tableCard.showTable();
        }
    }

    /**
     * Converts a book into a table row.
     *
     * @param b       the book
     * @param members member service used to show member names
     * @return the row values
     */
    static Object[] toRow(Book b, MemberService members) {
        String extra = "-";
        if (b.getStatus() == BookStatus.ISSUED) {
            extra = memberLabel(b.getIssuedTo(), members) + (b.getDueDate().isEmpty() ? "" : "  |  due " + b.getDueDate());
            if (b.getIssuedTo().isEmpty()) extra = "(manual)" + (b.getDueDate().isEmpty() ? "" : " due " + b.getDueDate());
        } else if (b.getStatus() == BookStatus.RESERVED) {
            extra = b.getReservedBy().isEmpty() ? "Reserved (library hold)"
                    : "Reserved by " + memberLabel(b.getReservedBy(), members);
        }
        return new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCategory().getDisplayName(),
                b.getStatus().getDisplayName(), extra};
    }

    /**
     * Builds "MEM-001 (Name)" for a member ID.
     *
     * @param memberId the member ID
     * @param members  member service
     * @return the label
     */
    static String memberLabel(String memberId, MemberService members) {
        Member m = members.findById(memberId);
        return m == null ? memberId : memberId + " (" + m.getName() + ")";
    }

    /**
     * Fills the form from the selected table row.
     */
    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
        Book b = frame.getSystem().getLibraryService().findById(id);
        if (b == null) return;
        idField.setText(b.getId());
        titleField.setText(b.getTitle());
        authorField.setText(b.getAuthor());
        categoryCombo.setSelectedItem(b.getCategory());
        statusCombo.setSelectedItem(b.getStatus());
    }

    /**
     * Adds a new book.
     */
    private void doAdd() {
        try {
            Book b = new Book(idField.getText(), titleField.getText(), authorField.getText(),
                    (Category) categoryCombo.getSelectedItem());
            frame.getSystem().getLibraryService().addBook(b);
            frame.dataChanged();
            frame.setStatus("Book '" + b.getTitle() + "' (" + b.getId() + ") added.");
            clearForm();
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Updates the book whose ID is in the form.
     */
    private void doUpdate() {
        try {
            Book b = frame.getSystem().getLibraryService().updateBook(idField.getText(), titleField.getText(),
                    authorField.getText(), (Category) categoryCombo.getSelectedItem());
            frame.dataChanged();
            frame.setStatus("Book " + b.getId() + " updated.");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Deletes the book whose ID is in the form, after confirmation.
     */
    private void doDelete() {
        LibraryService lib = frame.getSystem().getLibraryService();
        try {
            Book b = lib.requireBook(idField.getText());
            if (!UIStyle.confirm(this, "Delete book '" + b.getTitle() + "' (" + b.getId() + ")?\nThis cannot be undone.",
                    "Confirm Delete")) {
                return;
            }
            lib.deleteBook(b.getId());
            frame.dataChanged();
            frame.setStatus("Book '" + b.getTitle() + "' deleted.");
            clearForm();
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Sets the status chosen in the combo box on the book in the form.
     */
    private void doSetStatus() {
        try {
            Book b = frame.getSystem().getLibraryService().setStatus(idField.getText(),
                    (BookStatus) statusCombo.getSelectedItem());
            frame.dataChanged();
            frame.setStatus("Status of " + b.getId() + " set to " + b.getStatus().getDisplayName() + ".");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Clears the form and table selection.
     */
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        categoryCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        table.clearSelection();
        idField.requestFocusInWindow();
    }

    /**
     * Reloads the table.
     */
    @Override
    public void refresh() {
        reloadTable();
    }
}
