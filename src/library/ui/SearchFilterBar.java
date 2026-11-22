package library.ui;

import library.model.Book;
import library.model.BookStatus;
import library.model.Category;
import library.service.LibraryService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable search and filter bar for book tables: a search text field, a search-by combo
 * (Book ID / Title / Author), a status filter, a category filter and a Reset button.
 * Every change notifies a listener so the table updates live. The actual matching is
 * done by {@link LibraryService#searchAndFilter(String, String, String, String)}.
 *
 * @author Usman
 */
public class SearchFilterBar extends UIStyle.RoundedPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Search text input. */
    private final JTextField searchField = UIStyle.createTextField(12);
    /** Search field selector. */
    private final JComboBox<String> searchBy = UIStyle.createComboBox(new String[]{
            LibraryService.SEARCH_BY_ID, LibraryService.SEARCH_BY_TITLE, LibraryService.SEARCH_BY_AUTHOR});
    /** Status filter. */
    private final JComboBox<String> statusFilter;
    /** Category filter. */
    private final JComboBox<String> categoryFilter;
    /** Listener run whenever any criterion changes. */
    private transient Runnable onChange = () -> { };
    /** Suppresses change events during reset. */
    private boolean resetting;

    /**
     * Creates the search and filter bar.
     */
    public SearchFilterBar() {
        super(new GridBagLayout());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));

        List<String> statuses = new ArrayList<>();
        statuses.add(LibraryService.ALL);
        for (BookStatus s : BookStatus.values()) statuses.add(s.getDisplayName());
        statusFilter = UIStyle.createComboBox(statuses.toArray(new String[0]));

        List<String> cats = new ArrayList<>();
        cats.add(LibraryService.ALL);
        for (Category c : Category.values()) cats.add(c.getDisplayName());
        categoryFilter = UIStyle.createComboBox(cats.toArray(new String[0]));

        searchField.setToolTipText("Type to search (case-insensitive, partial match)");
        JButton reset = UIStyle.createButton("Reset", UIStyle.NEUTRAL);
        reset.addActionListener(e -> reset());

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(UIStyle.iconLabel("\uD83D\uDD0D", "Search", UIStyle.SMALL_BOLD, UIStyle.MUTED), c);
        c.weightx = 1;
        searchField.setMinimumSize(new Dimension(120, 32));
        add(UIStyle.height(searchField, 32), c);
        c.weightx = 0;
        add(searchBy, c);
        add(UIStyle.formLabel("Status"), c);
        add(statusFilter, c);
        add(UIStyle.formLabel("Category"), c);
        add(categoryFilter, c);
        c.insets = new Insets(0, 0, 0, 0);
        add(reset, c);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fire();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fire();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fire();
            }
        });
        searchBy.addActionListener(e -> fire());
        statusFilter.addActionListener(e -> fire());
        categoryFilter.addActionListener(e -> fire());
    }

    /**
     * Sets the listener notified when search or filter criteria change.
     *
     * @param listener the listener
     */
    public void setOnChange(Runnable listener) {
        this.onChange = listener == null ? () -> { } : listener;
    }

    /**
     * Notifies the listener unless a reset is in progress.
     */
    private void fire() {
        if (!resetting) onChange.run();
    }

    /**
     * Clears all criteria and notifies the listener once.
     */
    public void reset() {
        resetting = true;
        searchField.setText("");
        searchBy.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        categoryFilter.setSelectedIndex(0);
        resetting = false;
        onChange.run();
    }

    /**
     * Applies the current criteria using the service.
     *
     * @param service the library service
     * @return matching books
     */
    public List<Book> apply(LibraryService service) {
        return service.searchAndFilter(searchField.getText(), (String) searchBy.getSelectedItem(),
                (String) statusFilter.getSelectedItem(), (String) categoryFilter.getSelectedItem());
    }

    /**
     * Returns whether any criterion is active.
     *
     * @return true if search text or a filter is set
     */
    public boolean isActive() {
        return !searchField.getText().trim().isEmpty() || statusFilter.getSelectedIndex() > 0
                || categoryFilter.getSelectedIndex() > 0;
    }
}
