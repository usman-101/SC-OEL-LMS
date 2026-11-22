package library.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/**
 * Reusable dashboard statistic card: a white rounded card with a coloured accent bar,
 * an icon bubble, a large number and a label.
 *
 * @author Usman
 */
public class StatCard extends UIStyle.RoundedPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Label showing the number. */
    private final JLabel valueLabel;
    /** Accent colour of the card. */
    private final Color accent;

    /**
     * Creates a statistic card.
     *
     * @param title  the label under the number, e.g. "Total Books"
     * @param icon   a short icon text (emoji or symbol)
     * @param accent the accent colour
     */
    public StatCard(String title, String icon, Color accent) {
        super(new BorderLayout(14, 0));
        this.accent = accent;
        setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 18));
        setPreferredSize(new Dimension(240, 112));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 38));
                int d = Math.min(getWidth(), getHeight());
                g2.fillOval((getWidth() - d) / 2, (getHeight() - d) / 2, d, d);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(accent);
        iconLabel.setPreferredSize(new Dimension(56, 56));

        valueLabel = UIStyle.label("0", new Font(UIStyle.FONT_FAMILY, Font.BOLD, 34), UIStyle.TEXT);
        JLabel titleLabel = UIStyle.label(title, UIStyle.SMALL_BOLD, UIStyle.MUTED);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(valueLabel);
        text.add(titleLabel);

        add(iconLabel, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
    }

    /**
     * Updates the number shown on the card.
     *
     * @param value the new value
     */
    public void setValue(int value) {
        valueLabel.setText(String.valueOf(value));
    }

    /**
     * Returns the number currently shown.
     *
     * @return the displayed value text
     */
    public String getValueText() {
        return valueLabel.getText();
    }

    /**
     * Paints the card and a coloured accent bar on its left edge.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(accent);
        g2.fillRoundRect(0, 0, 8, getHeight() - 1, 8, 8);
        g2.fillRect(4, 0, 4, getHeight() - 1);
        g2.dispose();
    }
}
