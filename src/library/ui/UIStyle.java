package library.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.function.IntPredicate;

/**
 * Central place for the application's look: colour palette, fonts and factory methods
 * for consistently styled buttons, text fields, combo boxes, cards, tables and dialogs.
 *
 * @author Usman
 */
public final class UIStyle {

    // ==================== COLOURS ====================

    /** Dark navy used for the sidebar and branding. */
    public static final Color NAVY = new Color(0x1F2A44);
    /** Slightly lighter navy for sidebar hover/selection. */
    public static final Color NAVY_LIGHT = new Color(0x2C3A5C);
    /** Primary accent blue. */
    public static final Color ACCENT = new Color(0x3B82F6);
    /** Darker accent blue (hover). */
    public static final Color ACCENT_DARK = new Color(0x2563EB);
    /** Light grey application background. */
    public static final Color BACKGROUND = new Color(0xF4F6FA);
    /** White card background. */
    public static final Color CARD = Color.WHITE;
    /** Main text colour. */
    public static final Color TEXT = new Color(0x1E293B);
    /** Secondary (muted) text colour. */
    public static final Color MUTED = new Color(0x64748B);
    /** Border colour for inputs and cards. */
    public static final Color BORDER = new Color(0xE2E8F0);
    /** Green for success / available. */
    public static final Color SUCCESS = new Color(0x10B981);
    /** Amber for warnings / reserved. */
    public static final Color WARNING = new Color(0xF59E0B);
    /** Red for errors / overdue / delete. */
    public static final Color DANGER = new Color(0xEF4444);
    /** Purple for members. */
    public static final Color PURPLE = new Color(0x8B5CF6);
    /** Grey for neutral buttons. */
    public static final Color NEUTRAL = new Color(0x94A3B8);
    /** Alternate table row colour. */
    public static final Color ROW_ALT = new Color(0xF8FAFC);
    /** Table selection colour. */
    public static final Color SELECTION = new Color(0xDBEAFE);
    /** Background for highlighted (overdue) rows. */
    public static final Color OVERDUE_ROW = new Color(0xFEE2E2);
    /** Text colour for highlighted (overdue) rows. */
    public static final Color OVERDUE_TEXT = new Color(0xB91C1C);

    // ==================== FONTS ====================

    /** Font family: Segoe UI when installed, otherwise SansSerif. */
    public static final String FONT_FAMILY = pickFontFamily();
    /** Large title font. */
    public static final Font TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    /** Section header font. */
    public static final Font HEADER = new Font(FONT_FAMILY, Font.BOLD, 17);
    /** Normal body font. */
    public static final Font BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    /** Bold body font. */
    public static final Font BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    /** Small font. */
    public static final Font SMALL = new Font(FONT_FAMILY, Font.PLAIN, 13);
    /** Small bold font. */
    public static final Font SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);

    /** Minimum window size required by the specification. */
    public static final Dimension MIN_WINDOW = new Dimension(1100, 700);

    /**
     * Utility class; not instantiable.
     */
    private UIStyle() {
    }

    /**
     * Picks Segoe UI if available, otherwise the logical SansSerif font.
     *
     * @return the font family name
     */
    private static String pickFontFamily() {
        try {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            if (Arrays.asList(names).contains("Segoe UI")) return "Segoe UI";
        } catch (Exception e) {
            // headless or unusual environment: fall through
        }
        return Font.SANS_SERIF;
    }

    /**
     * Applies global Swing defaults (fonts, dialog colours) so standard components match the theme.
     */
    public static void applyGlobalDefaults() {
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
        UIManager.put("Label.font", BODY);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("TextField.font", BODY);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("CheckBox.font", SMALL);
        UIManager.put("Spinner.font", BODY);
        UIManager.put("ToolTip.font", SMALL);
    }

    // ==================== BUTTONS ====================

    /**
     * Creates a flat, rounded, coloured button with hover effect and hand cursor.
     *
     * @param text  button text
     * @param color background colour
     * @return the button
     */
    public static JButton createButton(String text, Color color) {
        return new FlatButton(text, color);
    }

    /**
     * Creates a primary (accent blue) button.
     *
     * @param text button text
     * @return the button
     */
    public static JButton primaryButton(String text) {
        return createButton(text, ACCENT);
    }

    /**
     * A flat button that paints a rounded background and darkens on hover/press.
     *
     * @author Usman
     */
    public static class FlatButton extends JButton {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Base background colour. */
        private final Color base;

        /**
         * Creates a flat button.
         *
         * @param text  the text
         * @param color the base colour
         */
        public FlatButton(String text, Color color) {
            super(text);
            this.base = color;
            setFont(BODY_BOLD);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        }

        /**
         * Paints the rounded background, then the text.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = base;
            if (!isEnabled()) {
                c = NEUTRAL;
            } else if (getModel().isPressed()) {
                c = base.darker().darker();
            } else if (getModel().isRollover()) {
                c = base.darker();
            }
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================== INPUTS ====================

    /**
     * Border used by text inputs: thin grey line plus inner padding.
     *
     * @return the border
     */
    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    /**
     * Creates a styled text field.
     *
     * @param columns preferred width in columns
     * @return the field
     */
    public static JTextField createTextField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(BODY);
        f.setBorder(inputBorder());
        return f;
    }

    /**
     * Creates a styled password field.
     *
     * @param columns preferred width in columns
     * @return the field
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        f.setFont(BODY);
        f.setBorder(inputBorder());
        return f;
    }

    /**
     * Creates a styled combo box.
     *
     * @param items the items
     * @param <T>   item type
     * @return the combo box
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(BODY);
        c.setBackground(Color.WHITE);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    /**
     * Creates a form label (small, bold, muted).
     *
     * @param text the text
     * @return the label
     */
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL_BOLD);
        l.setForeground(MUTED);
        return l;
    }

    /**
     * Creates a label with the given font and colour.
     *
     * @param text  the text
     * @param font  the font
     * @param color the colour
     * @return the label
     */
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /**
     * Creates a clickable hyperlink-style label.
     *
     * @param html   label text (HTML allowed)
     * @param action action run on click
     * @return the label
     */
    public static JLabel linkLabel(String html, Runnable action) {
        JLabel l = new JLabel("<html>" + html + "</html>");
        l.setFont(SMALL);
        l.setForeground(MUTED);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return l;
    }

    /**
     * Creates a small rounded coloured badge (e.g. the role badge in the header).
     *
     * @param text  badge text
     * @param color badge colour
     * @return the badge label
     */
    public static JLabel badge(String text, Color color) {
        JLabel l = new JLabel(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(SMALL_BOLD);
        l.setForeground(Color.WHITE);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
        return l;
    }

    /**
     * Icon that draws a short emoji/symbol string with an emoji-capable font, so symbols
     * render correctly next to text that uses Segoe UI (which has no emoji glyphs).
     *
     * @author Usman
     */
    public static class EmojiIcon implements javax.swing.Icon {

        /** The symbol text to draw. */
        private final String symbol;
        /** Font used for the symbol. */
        private final Font font;
        /** Colour used for monochrome symbols. */
        private final Color color;

        /**
         * Creates an emoji icon.
         *
         * @param symbol the symbol text
         * @param size   font size in points
         * @param color  colour for monochrome glyphs
         */
        public EmojiIcon(String symbol, int size, Color color) {
            this.symbol = symbol;
            this.font = new Font("Segoe UI Emoji", Font.PLAIN, size);
            this.color = color;
        }

        /**
         * Paints the symbol.
         *
         * @param c the component
         * @param g the graphics context
         * @param x left position
         * @param y top position
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(color);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(symbol, x, y + fm.getAscent());
            g2.dispose();
        }

        /**
         * Returns the icon width.
         *
         * @return width in pixels
         */
        @Override
        public int getIconWidth() {
            return (int) Math.ceil(font.getSize() * 1.35);
        }

        /**
         * Returns the icon height.
         *
         * @return height in pixels
         */
        @Override
        public int getIconHeight() {
            return (int) Math.ceil(font.getSize() * 1.45);
        }
    }

    /**
     * Creates a label made of an emoji icon followed by text.
     *
     * @param symbol emoji/symbol
     * @param text   label text
     * @param font   text font
     * @param color  text colour
     * @return the label
     */
    public static JLabel iconLabel(String symbol, String text, Font font, Color color) {
        JLabel l = label(text, font, color);
        l.setIcon(new EmojiIcon(symbol, font.getSize(), color));
        l.setIconTextGap(8);
        return l;
    }

    // ==================== CARDS ====================

    /**
     * A white panel with rounded corners and a subtle border.
     *
     * @author Usman
     */
    public static class RoundedPanel extends JPanel {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Corner radius. */
        private final int radius;
        /** Fill colour. */
        private final Color fill;

        /**
         * Creates a rounded white card with the given layout.
         *
         * @param layout the layout manager
         */
        public RoundedPanel(LayoutManager layout) {
            this(layout, CARD, 16);
        }

        /**
         * Creates a rounded card.
         *
         * @param layout the layout manager
         * @param fill   fill colour
         * @param radius corner radius
         */
        public RoundedPanel(LayoutManager layout, Color fill, int radius) {
            super(layout);
            this.fill = fill;
            this.radius = radius;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        }

        /**
         * Paints the rounded background and border.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Creates a page header with a title and a subtitle.
     *
     * @param title    the title
     * @param subtitle the subtitle
     * @return the header panel
     */
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        JLabel t = label(title, TITLE, TEXT);
        JLabel s = label(subtitle, SMALL, MUTED);
        p.add(t);
        p.add(javax.swing.Box.createVerticalStrut(2));
        p.add(s);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return p;
    }

    // ==================== TABLES ====================

    /**
     * Creates a non-editable table model with the given column names.
     *
     * @param columns column names
     * @return the model
     */
    public static DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Styles a table: row height 28, alternating rows, bold header, selection colour,
     * auto-resize and sorting via {@link TableRowSorter}.
     *
     * @param table the table to style
     */
    public static void styleTable(JTable table) {
        styleTable(table, null);
    }

    /**
     * Styles a table and highlights rows (in red) for which the predicate is true.
     *
     * @param table     the table
     * @param highlight predicate on the MODEL row index, or null for none
     */
    public static void styleTable(JTable table, IntPredicate highlight) {
        table.setRowHeight(28);
        table.setFont(BODY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setAutoCreateRowSorter(false);
        if (table.getModel() instanceof DefaultTableModel) {
            table.setRowSorter(new TableRowSorter<>(table.getModel()));
        }
        StripedRenderer renderer = new StripedRenderer(highlight);
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(Number.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
        table.setDefaultRenderer(Long.class, renderer);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    /**
     * Wraps a table in a borderless scroll pane with a white viewport.
     *
     * @param table the table
     * @return the scroll pane
     */
    public static JScrollPane scroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /**
     * Cell renderer with alternating row colours, padding and optional red highlighting.
     *
     * @author Usman
     */
    public static class StripedRenderer extends DefaultTableCellRenderer {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Predicate on model row index deciding which rows are highlighted. */
        private final transient IntPredicate highlight;

        /**
         * Creates the renderer.
         *
         * @param highlight predicate on model row index, or null
         */
        public StripedRenderer(IntPredicate highlight) {
            this.highlight = highlight;
        }

        /**
         * Returns the configured renderer component for a cell.
         *
         * @param table      the table
         * @param value      the cell value
         * @param isSelected whether the cell is selected
         * @param hasFocus   whether the cell has focus
         * @param row        the view row
         * @param column     the view column
         * @return the renderer component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setFont(BODY);
            int modelRow = table.convertRowIndexToModel(row);
            boolean hot = highlight != null && highlight.test(modelRow);
            if (isSelected) {
                setBackground(SELECTION);
                setForeground(hot ? OVERDUE_TEXT : TEXT);
            } else if (hot) {
                setBackground(OVERDUE_ROW);
                setForeground(OVERDUE_TEXT);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setForeground(TEXT);
            }
            setHorizontalAlignment(value instanceof Number ? SwingConstants.CENTER : SwingConstants.LEFT);
            return this;
        }
    }

    /**
     * Bold table header renderer with a light background and bottom accent line.
     *
     * @author Usman
     */
    public static class HeaderRenderer extends DefaultTableCellRenderer {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Returns the configured header component.
         *
         * @param table      the table
         * @param value      the header value
         * @param isSelected unused
         * @param hasFocus   unused
         * @param row        unused
         * @param column     the column
         * @return the header component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(SMALL_BOLD);
            setForeground(NAVY);
            setBackground(new Color(0xEEF2F7));
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }

    // ==================== DIALOGS ====================

    /**
     * Shows an error dialog.
     *
     * @param parent  parent component
     * @param message the message
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an information dialog.
     *
     * @param parent  parent component
     * @param message the message
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a Yes/No confirmation dialog.
     *
     * @param parent  parent component
     * @param message the question
     * @param title   dialog title
     * @return true if the user chose Yes
     */
    public static boolean confirm(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Gives a component a fixed preferred height while keeping its preferred width.
     *
     * @param c      the component
     * @param height the height
     * @param <T>    component type
     * @return the same component
     */
    public static <T extends JComponent> T height(T c, int height) {
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, height));
        return c;
    }

    /**
     * Truncates text with an ellipsis to fit a pixel width.
     *
     * @param text  the text
     * @param fm    font metrics
     * @param width maximum width in pixels
     * @return the possibly truncated text
     */
    public static String ellipsize(String text, FontMetrics fm, int width) {
        if (fm.stringWidth(text) <= width) return text;
        String s = text;
        while (s.length() > 1 && fm.stringWidth(s + "...") > width) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "...";
    }
}
