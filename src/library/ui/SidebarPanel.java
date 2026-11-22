package library.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Frosted dark glass sidebar navigation used by the Admin and Student panels.
 * The selected item is shown as a glossy liquid-blue pill.
 *
 * @author Usman
 */
public class SidebarPanel extends JPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Container for the navigation items. */
    private final JPanel items = new JPanel();
    /** Container for bottom items (logout). */
    private final JPanel bottom = new JPanel();
    /** All navigation buttons. */
    private final transient List<NavButton> buttons = new ArrayList<>();

    /**
     * Creates a sidebar with a section title.
     *
     * @param sectionTitle text shown above the items, e.g. "LIBRARIAN"
     */
    public SidebarPanel(String sectionTitle) {
        super(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(215, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 10, 14, 10));

        items.setOpaque(false);
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        javax.swing.JLabel title = UIStyle.label(sectionTitle, UIStyle.SMALL_BOLD, UIStyle.MUTED);
        title.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        items.add(title);

        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        add(items, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Paints the frosted dark rail with a glowing blue hairline on its right edge.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(6, 10, 20, 215), 0, getHeight(), new Color(8, 16, 34, 185)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(96, 165, 250, 110), 0, getHeight(), new Color(96, 165, 250, 10)));
        g2.fillRect(getWidth() - 1, 0, 1, getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Adds a navigation item.
     *
     * @param icon   icon text (emoji or symbol)
     * @param text   item text
     * @param action action run when clicked; the item becomes selected
     * @return the created button
     */
    public JButton addItem(String icon, String text, Runnable action) {
        NavButton b = new NavButton(icon, text);
        b.addActionListener(e -> {
            select(b);
            action.run();
        });
        buttons.add(b);
        items.add(b);
        items.add(Box.createVerticalStrut(4));
        return b;
    }

    /**
     * Adds an item at the bottom of the sidebar (not selectable, e.g. Logout).
     *
     * @param icon   icon text
     * @param text   item text
     * @param action action run when clicked
     * @return the created button
     */
    public JButton addBottomItem(String icon, String text, Runnable action) {
        NavButton b = new NavButton(icon, text);
        b.addActionListener(e -> action.run());
        bottom.add(b);
        return b;
    }

    /**
     * Marks a navigation button as selected.
     *
     * @param selected the button to select
     */
    public void select(JButton selected) {
        for (NavButton b : buttons) {
            b.setSelectedItem(b == selected);
        }
    }

    /**
     * Selects the item at the given index without running its action.
     *
     * @param index the item index
     */
    public void selectIndex(int index) {
        if (index >= 0 && index < buttons.size()) select(buttons.get(index));
    }

    /**
     * A sidebar navigation button.
     *
     * @author Usman
     */
    private static class NavButton extends JButton {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Whether this item is the selected page. */
        private boolean selectedItem;

        /**
         * Creates a navigation button.
         *
         * @param icon emoji icon text
         * @param text the text
         */
        NavButton(String icon, String text) {
            super(text);
            setIcon(new UIStyle.EmojiIcon(icon, 15, Color.WHITE));
            setIconTextGap(10);
            setFont(UIStyle.BODY);
            setForeground(UIStyle.TEXT);
            setHorizontalAlignment(LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 10));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        /**
         * Sets whether this button is the selected item.
         *
         * @param selected true if selected
         */
        void setSelectedItem(boolean selected) {
            this.selectedItem = selected;
            setFont(selected ? UIStyle.BODY_BOLD : UIStyle.BODY);
            setForeground(selected ? Color.WHITE : UIStyle.TEXT);
            repaint();
        }

        /**
         * Paints the hover glass or the selected liquid-blue pill.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selectedItem) {
                g2.setColor(UIStyle.alpha(UIStyle.ACCENT_BRIGHT, 45));
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
                UIStyle.paintLiquid(g, 2, 2, getWidth() - 4, getHeight() - 4, 14, UIStyle.ACCENT, 0f);
            } else {
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 16));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 14, 14);
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
