package library.ui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.function.IntPredicate;

/**
 * Central place for the application's "liquid glass" look: a black and deep-blue palette,
 * fonts, the Swing theme set-up, and factory methods for glass buttons, inputs, combo boxes,
 * frosted cards, dark tables and dialogs. Every screen gets its look from this class.
 *
 * @author Usman
 */
public final class UIStyle {

    // ==================== COLOURS ====================

    /** Deepest background colour (near black). */
    public static final Color BG_DEEP = new Color(0x04060C);
    /** Solid dark surface used behind dialogs and popups. */
    public static final Color SURFACE = new Color(0x0B1220);
    /** Dark navy used for glass tints. */
    public static final Color NAVY = new Color(0x0A1428);
    /** Slightly lighter navy. */
    public static final Color NAVY_LIGHT = new Color(0x13213D);
    /** Primary accent blue (buttons, selection). */
    public static final Color ACCENT = new Color(0x2563EB);
    /** Bright blue for highlights, links and focus rings. */
    public static final Color ACCENT_BRIGHT = new Color(0x60A5FA);
    /** Darker accent blue. */
    public static final Color ACCENT_DARK = new Color(0x1D4ED8);
    /** Cyan highlight. */
    public static final Color CYAN = new Color(0x22D3EE);
    /** Application background (used where a solid colour is needed). */
    public static final Color BACKGROUND = BG_DEEP;
    /** Card colour (glass tint). */
    public static final Color CARD = new Color(0x0F1A2E);
    /** Main text colour (cool white). */
    public static final Color TEXT = new Color(0xE6EDF7);
    /** Secondary (muted) text colour. */
    public static final Color MUTED = new Color(0x8A9BB8);
    /** Hairline border colour (translucent white). */
    public static final Color BORDER = new Color(255, 255, 255, 34);
    /** Sky blue used for positive actions / available. */
    public static final Color SUCCESS = new Color(0x0EA5E9);
    /** Indigo used for status changes / reserved. */
    public static final Color WARNING = new Color(0x6366F1);
    /** Red used only for destructive actions and overdue items. */
    public static final Color DANGER = new Color(0xEF4444);
    /** Violet-blue used for members. */
    public static final Color PURPLE = new Color(0x818CF8);
    /** Slate used for neutral buttons. */
    public static final Color NEUTRAL = new Color(0x334155);
    /** Input field background. */
    public static final Color INPUT_BG = new Color(0x0A1222);
    /** Table row colour. */
    public static final Color ROW = new Color(0x0B1322);
    /** Alternate table row colour. */
    public static final Color ROW_ALT = new Color(0x0F192C);
    /** Table header background. */
    public static final Color HEADER_BG = new Color(0x0C1B36);
    /** Table grid line colour. */
    public static final Color GRID = new Color(0x18263F);
    /** Table selection colour. */
    public static final Color SELECTION = new Color(0x1E40AF);
    /** Background for highlighted (overdue) rows. */
    public static final Color OVERDUE_ROW = new Color(0x3A0F18);
    /** Text colour for highlighted (overdue) rows. */
    public static final Color OVERDUE_TEXT = new Color(0xFCA5A5);

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
     * Returns a colour with a new alpha value.
     *
     * @param c     the colour
     * @param alpha alpha 0-255
     * @return the translucent colour
     */
    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    /**
     * Mixes a colour towards white (positive amount) or black (negative amount).
     *
     * @param c      the colour
     * @param amount -1..1
     * @return the adjusted colour
     */
    public static Color shade(Color c, float amount) {
        int target = amount >= 0 ? 255 : 0;
        float a = Math.abs(amount);
        return new Color(Math.round(c.getRed() + (target - c.getRed()) * a),
                Math.round(c.getGreen() + (target - c.getGreen()) * a),
                Math.round(c.getBlue() + (target - c.getBlue()) * a), c.getAlpha());
    }

    // ==================== THEME ====================

    /**
     * Installs the cross-platform (Metal) look and feel with the dark glass colours, so that
     * dialogs, popups, check boxes and scroll bars match the theme. Call once at start-up,
     * on the event dispatch thread, before creating any window.
     */
    public static void installTheme() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            // keep the default look and feel
        }
        applyGlobalDefaults();
    }

    /**
     * Applies global Swing defaults (fonts and dark glass colours) for standard components
     * such as JOptionPane, tool tips, popups and scroll bars.
     */
    public static void applyGlobalDefaults() {
        ColorUIResource surface = new ColorUIResource(SURFACE);
        ColorUIResource text = new ColorUIResource(TEXT);
        ColorUIResource input = new ColorUIResource(INPUT_BG);
        ColorUIResource sel = new ColorUIResource(SELECTION);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
        UIManager.put("OptionPane.background", surface);
        UIManager.put("OptionPane.messageForeground", text);
        UIManager.put("Panel.background", surface);
        UIManager.put("control", surface);
        UIManager.put("Label.font", BODY);
        UIManager.put("Label.foreground", text);
        UIManager.put("Button.font", BODY_BOLD);
        UIManager.put("Button.background", new ColorUIResource(ACCENT));
        UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("Button.select", new ColorUIResource(ACCENT_DARK));
        UIManager.put("Button.focus", new ColorUIResource(ACCENT_BRIGHT));
        UIManager.put("Button.gradient", null);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(8, 20, 8, 20));
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("ComboBox.background", input);
        UIManager.put("ComboBox.foreground", text);
        UIManager.put("ComboBox.selectionBackground", sel);
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.WHITE));
        UIManager.put("List.background", input);
        UIManager.put("List.foreground", text);
        UIManager.put("List.selectionBackground", sel);
        UIManager.put("List.selectionForeground", new ColorUIResource(Color.WHITE));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(0x1E3A5F)));
        UIManager.put("PopupMenu.background", input);
        for (String k : new String[]{"TextField", "PasswordField", "FormattedTextField"}) {
            UIManager.put(k + ".font", BODY);
            UIManager.put(k + ".background", input);
            UIManager.put(k + ".foreground", text);
            UIManager.put(k + ".caretForeground", new ColorUIResource(ACCENT_BRIGHT));
            UIManager.put(k + ".selectionBackground", sel);
            UIManager.put(k + ".selectionForeground", new ColorUIResource(Color.WHITE));
            UIManager.put(k + ".inactiveForeground", new ColorUIResource(MUTED));
        }
        UIManager.put("CheckBox.font", SMALL);
        UIManager.put("CheckBox.foreground", new ColorUIResource(MUTED));
        UIManager.put("CheckBox.background", surface);
        UIManager.put("Spinner.font", BODY);
        UIManager.put("Spinner.background", input);
        UIManager.put("Spinner.foreground", text);
        UIManager.put("ToolTip.font", SMALL);
        UIManager.put("ToolTip.background", new ColorUIResource(0x111B2E));
        UIManager.put("ToolTip.foreground", text);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(0x1E3A5F)));
        UIManager.put("ScrollPane.background", new ColorUIResource(ROW));
        UIManager.put("Viewport.background", new ColorUIResource(ROW));
        UIManager.put("Table.background", new ColorUIResource(ROW));
        UIManager.put("Table.foreground", text);
        UIManager.put("TableHeader.background", new ColorUIResource(HEADER_BG));
        UIManager.put("ScrollBarUI", GlassScrollBarUI.class.getName());
        UIManager.put("ScrollBar.width", 12);
    }

    // ==================== GLASS PAINTING ====================

    /**
     * Paints a frosted glass surface: dark tint, soft white gradient, gradient hairline border
     * and a top sheen line.
     *
     * @param g      graphics
     * @param w      width
     * @param h      height
     * @param radius corner radius
     * @param tint   base tint colour (alpha is respected)
     */
    public static void paintGlass(Graphics g, int w, int h, int radius, Color tint) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, radius, radius);
        g2.setColor(tint);
        g2.fill(shape);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 22), 0, h, new Color(255, 255, 255, 5)));
        g2.fill(shape);
        // sheen across the top third
        g2.setClip(shape);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 26), 0, Math.max(1, h / 3f), new Color(255, 255, 255, 0)));
        g2.fillRect(0, 0, w, Math.max(1, h / 3));
        g2.setClip(null);
        g2.setStroke(new BasicStroke(1f));
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 70), 0, h, new Color(120, 170, 255, 22)));
        g2.draw(shape);
        g2.dispose();
    }

    /**
     * Paints a glossy "liquid" pill in the given colour (used by buttons, badges and selected items).
     *
     * @param g      graphics
     * @param x      left
     * @param y      top
     * @param w      width
     * @param h      height
     * @param arc    corner arc
     * @param base   base colour
     * @param bright extra brightness 0..1 (hover)
     */
    public static void paintLiquid(Graphics g, int x, int y, int w, int h, int arc, Color base, float bright) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1f, h - 1f, arc, arc);
        Color top = alpha(shade(base, 0.18f + bright), 235);
        Color bottom = alpha(shade(base, -0.35f + bright / 2), 225);
        g2.setPaint(new GradientPaint(0, y, top, 0, y + h, bottom));
        g2.fill(shape);
        g2.setClip(shape);
        g2.setPaint(new GradientPaint(0, y, new Color(255, 255, 255, 95), 0, y + h * 0.55f, new Color(255, 255, 255, 0)));
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 1, w - 4f, h * 0.55f, arc, arc));
        g2.setPaint(new GradientPaint(0, y + h * 0.6f, new Color(255, 255, 255, 0), 0, y + h, alpha(shade(base, 0.5f), 70)));
        g2.fillRect(x, y + h / 2, w, h / 2);
        g2.setClip(null);
        g2.setStroke(new BasicStroke(1f));
        g2.setPaint(new GradientPaint(0, y, new Color(255, 255, 255, 120), 0, y + h, alpha(shade(base, 0.3f), 90)));
        g2.draw(shape);
        g2.dispose();
    }

    // ==================== BUTTONS ====================

    /**
     * Creates a glossy liquid-glass pill button with hover glow and hand cursor.
     *
     * @param text  button text
     * @param color base colour
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
     * A liquid-glass pill button: translucent gradient body, glossy top highlight,
     * light rim and a soft glow on hover.
     *
     * @author Usman
     */
    public static class FlatButton extends JButton {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Base colour. */
        private final Color base;

        /**
         * Creates a glass button.
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
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        /**
         * Paints the glow (on hover), the liquid body and then the text.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            int arc = Math.min(h, 22);
            Color c = isEnabled() ? base : NEUTRAL;
            float bright = 0f;
            if (isEnabled() && getModel().isPressed()) {
                bright = -0.12f;
            } else if (isEnabled() && getModel().isRollover()) {
                bright = 0.12f;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(alpha(shade(c, 0.3f), 70));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
                g2.dispose();
            }
            paintLiquid(g, 2, 2, w - 4, h - 4, arc - 2, c, bright);
            setForeground(isEnabled() ? Color.WHITE : MUTED);
            super.paintComponent(g);
        }
    }

    // ==================== INPUTS ====================

    /**
     * Rounded outline border for inputs; glows blue while the component has focus.
     *
     * @author Usman
     */
    public static class RoundedFieldBorder extends AbstractBorder {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Inner padding. */
        private final Insets pad;

        /**
         * Creates the border.
         *
         * @param top    top padding
         * @param left   left padding
         * @param bottom bottom padding
         * @param right  right padding
         */
        public RoundedFieldBorder(int top, int left, int bottom, int right) {
            this.pad = new Insets(top, left, bottom, right);
        }

        /**
         * Paints the rounded outline.
         *
         * @param c      the component
         * @param g      graphics
         * @param x      left
         * @param y      top
         * @param width  width
         * @param height height
         */
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean focus = c.hasFocus() || (c instanceof JComboBox && ((JComboBox<?>) c).isPopupVisible());
            if (focus) {
                g2.setColor(alpha(ACCENT_BRIGHT, 60));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 14, 14);
            }
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(focus ? ACCENT_BRIGHT : new Color(0x22406B));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 12, 12);
            g2.dispose();
        }

        /**
         * Returns the padding.
         *
         * @param c the component
         * @return the insets
         */
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(pad.top, pad.left, pad.bottom, pad.right);
        }

        /**
         * Fills the given insets object with the padding.
         *
         * @param c      the component
         * @param insets the insets to fill
         * @return the insets
         */
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(pad.top, pad.left, pad.bottom, pad.right);
            return insets;
        }
    }

    /**
     * Fills a rounded dark input background behind a component.
     *
     * @param c the component
     * @param g graphics
     */
    static void paintInputBackground(JComponent c, Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean editable = !(c instanceof JTextField) || ((JTextField) c).isEditable();
        g2.setPaint(new GradientPaint(0, 0, editable ? new Color(0x0E1A30) : new Color(0x0A1020),
                0, c.getHeight(), editable ? INPUT_BG : new Color(0x080D18)));
        g2.fillRoundRect(1, 1, c.getWidth() - 2, c.getHeight() - 2, 12, 12);
        g2.dispose();
    }

    /**
     * Border used by text inputs.
     *
     * @return the border
     */
    public static Border inputBorder() {
        return new RoundedFieldBorder(7, 12, 7, 12);
    }

    /**
     * A dark rounded glass text field.
     *
     * @author Usman
     */
    public static class GlassTextField extends JTextField {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the field.
         *
         * @param columns preferred width in columns
         */
        public GlassTextField(int columns) {
            super(columns);
            styleInput(this);
        }

        /**
         * Paints the rounded background, then the text.
         *
         * @param g graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            paintInputBackground(this, g);
            super.paintComponent(g);
        }
    }

    /**
     * A dark rounded glass password field.
     *
     * @author Usman
     */
    public static class GlassPasswordField extends JPasswordField {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the field.
         *
         * @param columns preferred width in columns
         */
        public GlassPasswordField(int columns) {
            super(columns);
            styleInput(this);
        }

        /**
         * Paints the rounded background, then the text.
         *
         * @param g graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            paintInputBackground(this, g);
            super.paintComponent(g);
        }
    }

    /**
     * Applies the shared input look (transparent, light text, blue caret, rounded border,
     * border repaint on focus change).
     *
     * @param f the text field
     */
    private static void styleInput(JTextField f) {
        f.setOpaque(false);
        f.setFont(BODY);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT_BRIGHT);
        f.setSelectionColor(SELECTION);
        f.setSelectedTextColor(Color.WHITE);
        f.setDisabledTextColor(MUTED);
        f.setBorder(inputBorder());
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                f.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                f.repaint();
            }
        });
    }

    /**
     * Creates a styled text field.
     *
     * @param columns preferred width in columns
     * @return the field
     */
    public static JTextField createTextField(int columns) {
        return new GlassTextField(columns);
    }

    /**
     * Creates a styled password field.
     *
     * @param columns preferred width in columns
     * @return the field
     */
    public static JPasswordField createPasswordField(int columns) {
        return new GlassPasswordField(columns);
    }

    /**
     * Small chevron button used by combo boxes and spinners.
     *
     * @author Usman
     */
    public static class ArrowButton extends JButton {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Direction: SwingConstants.NORTH or SOUTH. */
        private final int direction;

        /**
         * Creates the arrow button.
         *
         * @param direction SwingConstants.NORTH or SwingConstants.SOUTH
         */
        public ArrowButton(int direction) {
            this.direction = direction;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFocusable(false);
            setRolloverEnabled(true);
            setBorder(BorderFactory.createEmptyBorder());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(28, 14));
        }

        /**
         * Paints the chevron.
         *
         * @param g graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? Color.WHITE : ACCENT_BRIGHT);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int s = 4;
            if (direction == SwingConstants.NORTH) {
                g2.drawPolyline(new int[]{cx - s, cx, cx + s}, new int[]{cy + s / 2, cy - s / 2, cy + s / 2}, 3);
            } else {
                g2.drawPolyline(new int[]{cx - s, cx, cx + s}, new int[]{cy - s / 2, cy + s / 2, cy - s / 2}, 3);
            }
            g2.dispose();
        }
    }

    /**
     * Combo box UI with a dark rounded body, blue chevron and dark popup list.
     *
     * @author Usman
     */
    public static class GlassComboBoxUI extends BasicComboBoxUI {

        /**
         * Creates the UI.
         */
        public GlassComboBoxUI() {
            super();
        }

        /**
         * Installs the UI and makes the combo transparent so the rounded body shows.
         *
         * @param c the combo box
         */
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
            c.setBorder(new RoundedFieldBorder(1, 4, 1, 2));
        }

        /**
         * Creates the chevron arrow button.
         *
         * @return the arrow button
         */
        @Override
        protected JButton createArrowButton() {
            return new ArrowButton(SwingConstants.SOUTH);
        }

        /**
         * Creates the popup with a dark border.
         *
         * @return the popup
         */
        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup p = new BasicComboPopup(comboBox);
            p.setBorder(BorderFactory.createLineBorder(new Color(0x22406B)));
            return p;
        }

        /**
         * Paints the rounded body and the current value text.
         *
         * @param g graphics
         * @param c the combo box
         */
        @Override
        public void paint(Graphics g, JComponent c) {
            paintInputBackground(c, g);
            Rectangle r = rectangleForCurrentValue();
            Object value = comboBox.getSelectedItem();
            if (value != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(comboBox.getFont());
                g2.setColor(comboBox.isEnabled() ? TEXT : MUTED);
                FontMetrics fm = g2.getFontMetrics();
                String text = ellipsize(value.toString(), fm, r.width - 10);
                g2.drawString(text, r.x + 8, r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    /**
     * Spinner UI with chevron buttons and a dark rounded body.
     *
     * @author Usman
     */
    public static class GlassSpinnerUI extends BasicSpinnerUI {

        /**
         * Creates the UI.
         */
        public GlassSpinnerUI() {
            super();
        }

        /**
         * Creates the "increase" button.
         *
         * @return the button
         */
        @Override
        protected Component createNextButton() {
            JButton b = new ArrowButton(SwingConstants.NORTH);
            installNextButtonListeners(b);
            return b;
        }

        /**
         * Creates the "decrease" button.
         *
         * @return the button
         */
        @Override
        protected Component createPreviousButton() {
            JButton b = new ArrowButton(SwingConstants.SOUTH);
            installPreviousButtonListeners(b);
            return b;
        }

        /**
         * Paints the rounded body before the children.
         *
         * @param g graphics
         * @param c the spinner
         */
        @Override
        public void update(Graphics g, JComponent c) {
            paintInputBackground(c, g);
            paint(g, c);
        }
    }

    /**
     * Applies the glass look to a spinner.
     *
     * @param s the spinner
     */
    public static void styleSpinner(JSpinner s) {
        s.setUI(new GlassSpinnerUI());
        s.setOpaque(false);
        s.setFont(BODY);
        s.setBorder(new RoundedFieldBorder(2, 6, 2, 4));
        if (s.getEditor() instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) s.getEditor()).getTextField();
            tf.setOpaque(false);
            tf.setForeground(TEXT);
            tf.setCaretColor(ACCENT_BRIGHT);
            tf.setFont(BODY);
            tf.setBorder(BorderFactory.createEmptyBorder());
            s.getEditor().setOpaque(false);
        }
    }

    /**
     * Creates a styled glass combo box.
     *
     * @param items the items
     * @param <T>   item type
     * @return the combo box
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setUI(new GlassComboBoxUI());
        c.setFont(BODY);
        c.setBackground(INPUT_BG);
        c.setForeground(TEXT);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.setRenderer(new GlassListRenderer<>());
        c.setMaximumRowCount(12);
        return c;
    }

    /**
     * Dark list renderer for combo box popups.
     *
     * @param <T> item type
     * @author Usman
     */
    public static class GlassListRenderer<T> extends JLabel implements ListCellRenderer<T> {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the renderer.
         */
        public GlassListRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        }

        /**
         * Configures the renderer for one list item.
         *
         * @param list         the list
         * @param value        the value
         * @param index        the index
         * @param isSelected   whether selected
         * @param cellHasFocus whether focused
         * @return this renderer
         */
        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());
            setFont(BODY);
            setBackground(isSelected ? SELECTION : INPUT_BG);
            setForeground(isSelected ? Color.WHITE : TEXT);
            return this;
        }
    }

    /**
     * Thin translucent scroll bar with a rounded blue glass thumb and no arrow buttons.
     *
     * @author Usman
     */
    public static class GlassScrollBarUI extends BasicScrollBarUI {

        /**
         * Creates the UI.
         */
        public GlassScrollBarUI() {
            super();
        }

        /**
         * Factory used by UIManager ("ScrollBarUI").
         *
         * @param c the scroll bar
         * @return a new UI instance
         */
        public static ComponentUI createUI(JComponent c) {
            return new GlassScrollBarUI();
        }

        /**
         * Returns an invisible zero-size button (no arrows).
         *
         * @return the button
         */
        private JButton zero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        /**
         * Creates the (hidden) decrease button.
         *
         * @param orientation orientation
         * @return the button
         */
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zero();
        }

        /**
         * Creates the (hidden) increase button.
         *
         * @param orientation orientation
         * @return the button
         */
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zero();
        }

        /**
         * Paints the track.
         *
         * @param g      graphics
         * @param c      component
         * @param bounds track bounds
         */
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
            g.setColor(new Color(0x09101D));
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        /**
         * Paints the rounded glass thumb.
         *
         * @param g      graphics
         * @param c      component
         * @param bounds thumb bounds
         */
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
            if (bounds.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean vertical = scrollbar.getOrientation() == JScrollBar.VERTICAL;
            int pad = 3;
            int x = bounds.x + (vertical ? pad : 1);
            int y = bounds.y + (vertical ? 1 : pad);
            int w = bounds.width - (vertical ? pad * 2 : 2);
            int h = bounds.height - (vertical ? 2 : pad * 2);
            g2.setColor(alpha(ACCENT_BRIGHT, isThumbRollover() ? 170 : 110));
            g2.fillRoundRect(x, y, w, h, 8, 8);
            g2.dispose();
        }
    }

    // ==================== LABELS ====================

    /**
     * Creates a form label (small, bold, muted blue-grey).
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
     * Creates a small liquid-glass badge (e.g. the role badge in the header).
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
                paintLiquid(g, 0, 0, getWidth(), getHeight(), getHeight(), color, 0f);
                super.paintComponent(g);
            }
        };
        l.setFont(SMALL_BOLD);
        l.setForeground(Color.WHITE);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        return l;
    }

    /**
     * Icon that draws a short emoji/symbol string with an emoji-capable font, so symbols
     * render correctly next to text that uses Segoe UI (which has no emoji glyphs).
     *
     * @author Usman
     */
    public static class EmojiIcon implements Icon {

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
     * A small glossy liquid-glass dot, used as a bullet.
     *
     * @author Usman
     */
    public static class LiquidDotIcon implements Icon {

        /** Dot diameter. */
        private final int size;
        /** Dot colour. */
        private final Color color;

        /**
         * Creates the dot icon.
         *
         * @param size  diameter in pixels
         * @param color colour
         */
        public LiquidDotIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        /**
         * Paints the dot with a soft glow and a highlight.
         *
         * @param c the component
         * @param g graphics
         * @param x left
         * @param y top
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(alpha(color, 60));
            g2.fillOval(x, y, size + 4, size + 4);
            g2.setPaint(new GradientPaint(x, y, shade(color, 0.35f), x, y + size, shade(color, -0.35f)));
            g2.fillOval(x + 2, y + 2, size, size);
            g2.setColor(new Color(255, 255, 255, 170));
            g2.fillOval(x + 2 + size / 4, y + 3, size / 2, size / 3);
            g2.dispose();
        }

        /**
         * Returns the icon width.
         *
         * @return width
         */
        @Override
        public int getIconWidth() {
            return size + 4;
        }

        /**
         * Returns the icon height.
         *
         * @return height
         */
        @Override
        public int getIconHeight() {
            return size + 4;
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
     * A frosted liquid-glass card with rounded corners, a gradient rim and a top sheen.
     * It is transparent, so the glowing background shows through.
     *
     * @author Usman
     */
    public static class RoundedPanel extends JPanel {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Corner radius. */
        private final int radius;
        /** Glass tint colour. */
        private final Color fill;

        /**
         * Creates a glass card with the given layout.
         *
         * @param layout the layout manager
         */
        public RoundedPanel(LayoutManager layout) {
            this(layout, new Color(12, 20, 38, 150), 22);
        }

        /**
         * Creates a glass card.
         *
         * @param layout the layout manager
         * @param fill   glass tint colour (alpha respected)
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
         * Paints the glass surface.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            paintGlass(g, getWidth(), getHeight(), radius, fill);
            super.paintComponent(g);
        }
    }

    /**
     * Creates a translucent black bar (header / status / welcome bars).
     *
     * @param topLine    draw a hairline at the top
     * @param bottomLine draw a hairline at the bottom
     * @return the bar panel (BorderLayout)
     */
    public static JPanel glassBar(boolean topLine, boolean bottomLine) {
        JPanel bar = new JPanel(new java.awt.BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(6, 10, 20, 205), 0, getHeight(), new Color(8, 14, 28, 175)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(96, 165, 250, 0), getWidth() / 2f, 0,
                        new Color(96, 165, 250, 90), true));
                if (topLine) g2.fillRect(0, 0, getWidth(), 1);
                if (bottomLine) g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        return bar;
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
        JLabel t = label(title, TITLE, Color.WHITE);
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
     * Styles a table: row height 28, alternating dark rows, bold blue header, blue selection,
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
        table.setBackground(ROW);
        table.setForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(GRID);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(Color.WHITE);
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
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    /**
     * Wraps a table in a dark scroll pane with a hairline border.
     *
     * @param table the table
     * @return the scroll pane
     */
    public static JScrollPane scroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x1A2B47)));
        sp.getViewport().setBackground(ROW);
        sp.setBackground(ROW);
        JPanel corner = new JPanel();
        corner.setBackground(HEADER_BG);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        return sp;
    }

    /**
     * Cell renderer with alternating dark rows, padding and optional red highlighting.
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
                setForeground(hot ? OVERDUE_TEXT : Color.WHITE);
            } else if (hot) {
                setBackground(OVERDUE_ROW);
                setForeground(OVERDUE_TEXT);
            } else {
                setBackground(row % 2 == 0 ? ROW : ROW_ALT);
                setForeground(TEXT);
            }
            setHorizontalAlignment(value instanceof Number ? SwingConstants.CENTER : SwingConstants.LEFT);
            return this;
        }
    }

    /**
     * Bold table header renderer: dark blue background, light blue text and a glowing
     * accent line at the bottom.
     *
     * @author Usman
     */
    public static class HeaderRenderer extends DefaultTableCellRenderer {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the header renderer.
         */
        public HeaderRenderer() {
            super();
        }

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
            setForeground(new Color(0x93C5FD));
            setBackground(HEADER_BG);
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
