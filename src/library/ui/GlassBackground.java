package library.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 * Window background for the liquid glass theme: a near-black base lit by soft, large
 * blue and cyan glows, with a darker vignette at the bottom. Glass cards placed on top
 * are translucent, so these glows shine through them.
 *
 * @author Usman
 */
public class GlassBackground extends JPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the background panel with the given layout.
     *
     * @param layout the layout manager
     */
    public GlassBackground(LayoutManager layout) {
        super(layout);
        setOpaque(true);
        setBackground(UIStyle.BG_DEEP);
    }

    /**
     * Paints the base colour, the blue glows and the vignette.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        g2.setPaint(new GradientPaint(0, 0, new Color(0x050913), 0, h, UIStyle.BG_DEEP));
        g2.fillRect(0, 0, w, h);
        float size = Math.max(w, h);
        glow(g2, w * 0.12f, h * 0.05f, size * 0.62f, new Color(37, 99, 235, 120));
        glow(g2, w * 0.95f, h * 0.92f, size * 0.55f, new Color(14, 165, 233, 70));
        glow(g2, w * 0.62f, h * 0.38f, size * 0.38f, new Color(29, 78, 216, 55));
        glow(g2, w * 0.02f, h * 0.98f, size * 0.30f, new Color(99, 102, 241, 40));
        g2.setPaint(new GradientPaint(0, h * 0.55f, new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 120)));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    /**
     * Paints one radial glow that fades to transparent.
     *
     * @param g2     graphics
     * @param cx     centre x
     * @param cy     centre y
     * @param radius glow radius
     * @param color  glow colour at the centre
     */
    private static void glow(Graphics2D g2, float cx, float cy, float radius, Color color) {
        if (radius <= 0) return;
        Color clear = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        Color mid = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 3);
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy), radius,
                new float[]{0f, 0.45f, 1f}, new Color[]{color, mid, clear}));
        g2.fillRect(0, 0, (int) Math.ceil(cx + radius), (int) Math.ceil(cy + radius));
    }
}
