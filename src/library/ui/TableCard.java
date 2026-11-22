package library.ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;

/**
 * A container that shows either a styled table or a friendly empty-state message
 * (for example "The library is empty. No books to display.") instead of a blank table.
 *
 * @author Usman
 */
public class TableCard extends JPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Card name of the table view. */
    private static final String TABLE = "table";
    /** Card name of the empty view. */
    private static final String EMPTY = "empty";
    /** Layout switching between the two views. */
    private final CardLayout layout = new CardLayout();
    /** Message shown in the empty view. */
    private final JLabel messageLabel = UIStyle.label("", UIStyle.HEADER, UIStyle.MUTED);
    /** Whether the empty view is currently shown. */
    private boolean emptyShown;

    /**
     * Creates the card around a table.
     *
     * @param table the table to show
     * @param icon  icon text for the empty state
     */
    public TableCard(JTable table, String icon) {
        setLayout(layout);
        setOpaque(false);
        add(UIStyle.scroll(table), TABLE);

        JPanel empty = new UIStyle.RoundedPanel(new GridBagLayout());
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        JLabel iconLabel = UIStyle.label(icon, new Font("Segoe UI Emoji", Font.PLAIN, 48), UIStyle.NEUTRAL);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(iconLabel);
        inner.add(javax.swing.Box.createVerticalStrut(10));
        inner.add(messageLabel);
        empty.add(inner);
        add(empty, EMPTY);
        showTable();
    }

    /**
     * Shows the table.
     */
    public void showTable() {
        layout.show(this, TABLE);
        emptyShown = false;
    }

    /**
     * Shows the empty-state message instead of the table.
     *
     * @param message the message to display
     */
    public void showEmpty(String message) {
        messageLabel.setText(message);
        layout.show(this, EMPTY);
        emptyShown = true;
    }

    /**
     * Returns whether the empty-state message is shown.
     *
     * @return true if the empty view is visible
     */
    public boolean isEmptyShown() {
        return emptyShown;
    }

    /**
     * Returns the current empty-state message.
     *
     * @return the message text
     */
    public String getMessage() {
        return messageLabel.getText();
    }
}
