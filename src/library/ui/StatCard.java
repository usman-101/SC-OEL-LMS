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
 * Reusable dashboard statistic card: a frosted glass card with an accent glow, a liquid-glass
 * icon orb, a large number and a label.
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
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 18));
        setPreferredSize(new Dimension(240, 112));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setPaint(new java.awt.RadialGradientPaint(new java.awt.geom.Point2D.Float(x + d * 0.35f, y + d * 0.3f),
                        d * 0.8f, new float[]{0f, 1f},
                        new Color[]{UIStyle.alpha(accent, 120), UIStyle.alpha(UIStyle.shade(accent, -0.6f), 70)}));
                g2.fillOval(x, y, d, d);
                g2.setPaint(new java.awt.GradientPaint(x, y, new Color(255, 255, 255, 140), x, y + d,
                        UIStyle.alpha(accent, 60)));
                g2.drawOval(x, y, d, d);
                g2.setPaint(new java.awt.GradientPaint(x, y, new Color(255, 255, 255, 90), x, y + d * 0.45f,
                        new Color(255, 255, 255, 0)));
                g2.fillOval(x + d / 5, y + 2, d * 3 / 5, d * 2 / 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(accent);
        iconLabel.setPreferredSize(new Dimension(56, 56));

        valueLabel = UIStyle.label("0", new Font(UIStyle.FONT_FAMILY, Font.BOLD, 34), Color.WHITE);
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
     * Paints the glass card with a soft accent-coloured glow in its corner and a glowing
     * accent line along the top edge.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
        float r = Math.max(getWidth(), getHeight()) * 0.75f;
        g2.setPaint(new java.awt.RadialGradientPaint(new java.awt.geom.Point2D.Float(0, 0), r,
                new float[]{0f, 1f}, new Color[]{UIStyle.alpha(accent, 70), UIStyle.alpha(accent, 0)}));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setPaint(new java.awt.GradientPaint(16, 0, UIStyle.alpha(accent, 0), getWidth() / 2f, 0,
                UIStyle.alpha(UIStyle.shade(accent, 0.3f), 220), true));
        g3.fillRect(16, 0, getWidth() - 32, 2);
        g3.dispose();
    }
}
