package library.ui;

import library.exception.LibraryException;
import library.model.User;
import library.service.LibrarySystem;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/**
 * The start window: a split layout with a branding panel on the left and a form card
 * on the right that switches between Sign In and {@link SignUpPanel}.
 *
 * @author Usman
 */
public class LoginFrame extends JFrame {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Card name for the sign-in form. */
    private static final String SIGN_IN = "signin";
    /** Card name for the sign-up form. */
    private static final String SIGN_UP = "signup";

    /** The application services. */
    private final transient LibrarySystem system;
    /** Layout switching between sign in and sign up. */
    private final CardLayout cards = new CardLayout();
    /** Panel holding the two forms. */
    private final JPanel formHolder = new JPanel(cards);
    /** Email input on the sign-in form. */
    private final JTextField emailField = UIStyle.createTextField(22);
    /** Password input on the sign-in form. */
    private final JPasswordField passwordField = UIStyle.createPasswordField(22);
    /** Sign-in button. */
    private final JButton signInButton = UIStyle.primaryButton("Sign In");
    /** The sign-up form. */
    private final SignUpPanel signUpPanel;

    /**
     * Creates the login window.
     *
     * @param system the application services
     */
    public LoginFrame(LibrarySystem system) {
        super("Library Book Management System - Sign In");
        this.system = system;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(UIStyle.MIN_WINDOW);
        setSize(UIStyle.MIN_WINDOW);
        setLocationRelativeTo(null);

        JPanel root = new GlassBackground(new GridLayout(1, 2));
        root.add(new BrandingPanel());

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        UIStyle.RoundedPanel card = new UIStyle.RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 34, 28, 34));
        card.setPreferredSize(new Dimension(440, 610));

        signUpPanel = new SignUpPanel(system, this);
        formHolder.setOpaque(false);
        formHolder.add(buildSignInForm(), SIGN_IN);
        formHolder.add(signUpPanel, SIGN_UP);
        card.add(formHolder, BorderLayout.CENTER);
        right.add(card);
        root.add(right);

        setContentPane(root);
        getRootPane().setDefaultButton(signInButton);
    }

    /**
     * Builds the sign-in form.
     *
     * @return the form panel
     */
    private JPanel buildSignInForm() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        addLeft(p, UIStyle.label("Welcome back", UIStyle.TITLE, Color.WHITE));
        addLeft(p, UIStyle.label("Sign in to continue to your library account", UIStyle.SMALL, UIStyle.MUTED));
        p.add(Box.createVerticalStrut(26));

        addLeft(p, UIStyle.formLabel("Email"));
        p.add(Box.createVerticalStrut(5));
        addLeft(p, fullWidth(emailField));
        p.add(Box.createVerticalStrut(14));

        addLeft(p, UIStyle.formLabel("Password"));
        p.add(Box.createVerticalStrut(5));
        addLeft(p, fullWidth(passwordField));
        p.add(Box.createVerticalStrut(6));

        JCheckBox show = new JCheckBox("Show password");
        show.setOpaque(false);
        show.setForeground(UIStyle.MUTED);
        char echo = passwordField.getEchoChar();
        show.addActionListener(e -> passwordField.setEchoChar(show.isSelected() ? (char) 0 : echo));
        addLeft(p, show);
        p.add(Box.createVerticalStrut(18));

        signInButton.addActionListener(e -> doSignIn());
        addLeft(p, fullWidth(signInButton));
        p.add(Box.createVerticalStrut(16));

        addLeft(p, UIStyle.linkLabel("Don't have an account? <b><font color='#60A5FA'>Sign Up</font></b>",
                this::showSignUp));
        p.add(Box.createVerticalGlue());

        JLabel hint = UIStyle.label("<html><div style='width:300px'>Tip: librarian accounts use an email like "
                + "<b>name.admin@library.com</b>. Any other email signs in as a student.</div></html>",
                UIStyle.SMALL, UIStyle.MUTED);
        addLeft(p, hint);
        return p;
    }

    /**
     * Adds a component left-aligned to a vertical box panel.
     *
     * @param p the panel
     * @param c the component
     */
    static void addLeft(JPanel p, JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(c);
    }

    /**
     * Lets a component stretch horizontally in a BoxLayout while keeping its height.
     *
     * @param c   the component
     * @param <T> component type
     * @return the same component
     */
    static <T extends JComponent> T fullWidth(T c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(38, c.getPreferredSize().height)));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, Math.max(38, c.getPreferredSize().height)));
        return c;
    }

    /**
     * Validates credentials through the AuthService and opens the main window on success.
     */
    private void doSignIn() {
        try {
            User user = system.getAuthService().signIn(emailField.getText(), new String(passwordField.getPassword()));
            system.getLibraryService().setActor(user.getEmail());
            MainFrame main = new MainFrame(system, user);
            main.setVisible(true);
            dispose();
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        } catch (RuntimeException ex) {
            UIStyle.showError(this, "Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Switches to the sign-up form.
     */
    public void showSignUp() {
        signUpPanel.reset();
        cards.show(formHolder, SIGN_UP);
        setTitle("Library Book Management System - Sign Up");
        getRootPane().setDefaultButton(signUpPanel.getSubmitButton());
    }

    /**
     * Switches to the sign-in form, optionally pre-filling the email.
     *
     * @param email email to pre-fill, or null
     */
    public void showSignIn(String email) {
        if (email != null) emailField.setText(email);
        passwordField.setText("");
        cards.show(formHolder, SIGN_IN);
        setTitle("Library Book Management System - Sign In");
        getRootPane().setDefaultButton(signInButton);
        passwordField.requestFocusInWindow();
    }

    /**
     * Left-hand branding area: floating liquid-glass orbs, a large title with a blue-to-cyan
     * gradient, a tagline and the feature list. It is transparent over the glowing background.
     *
     * @author Usman
     */
    private static class BrandingPanel extends JPanel {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Builds the branding panel.
         */
        BrandingPanel() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            JPanel inner = new JPanel();
            inner.setOpaque(false);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            JLabel logo = UIStyle.label("\uD83D\uDCDA", new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 50), Color.WHITE);
            addLeft(inner, logo);
            inner.add(Box.createVerticalStrut(14));
            java.awt.Font big = new java.awt.Font(UIStyle.FONT_FAMILY, java.awt.Font.BOLD, 36);
            addLeft(inner, UIStyle.label("Library Book", big, Color.WHITE));
            addLeft(inner, new GradientLabel("Management System", big));
            inner.add(Box.createVerticalStrut(12));
            addLeft(inner, UIStyle.label("Organise books, members and loans in one place.", UIStyle.BODY, new Color(0x9DB4D6)));
            inner.add(Box.createVerticalStrut(30));
            String[] features = {"Role-based Admin and Student panels", "Issue, return and reserve books",
                    "Overdue tracking and statistics", "Complete transaction history"};
            for (String f : features) {
                JLabel item = UIStyle.label(f, UIStyle.BODY, UIStyle.TEXT);
                item.setIcon(new UIStyle.LiquidDotIcon(10, UIStyle.ACCENT_BRIGHT));
                item.setIconTextGap(12);
                addLeft(inner, item);
                inner.add(Box.createVerticalStrut(10));
            }
            add(inner);
        }

        /**
         * Paints two floating liquid-glass orbs behind the text.
         *
         * @param g the graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            orb(g, getWidth() - 190, 40, 170);
            orb(g, 30, getHeight() - 170, 110);
        }

        /**
         * Paints one glass orb: translucent blue body, bright rim and a specular highlight.
         *
         * @param g    graphics
         * @param x    left
         * @param y    top
         * @param size diameter
         */
        private static void orb(Graphics g, int x, int y, int size) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new java.awt.RadialGradientPaint(new java.awt.geom.Point2D.Float(x + size * 0.35f, y + size * 0.3f),
                    size * 0.8f, new float[]{0f, 0.6f, 1f},
                    new Color[]{new Color(96, 165, 250, 70), new Color(37, 99, 235, 40), new Color(14, 30, 80, 20)}));
            g2.fillOval(x, y, size, size);
            g2.setStroke(new java.awt.BasicStroke(1.2f));
            g2.setPaint(new GradientPaint(x, y, new Color(255, 255, 255, 120), x + size, y + size, new Color(96, 165, 250, 30)));
            g2.drawOval(x, y, size, size);
            g2.setPaint(new GradientPaint(x, y, new Color(255, 255, 255, 110), x, y + size * 0.4f, new Color(255, 255, 255, 0)));
            g2.fillOval(x + size / 5, y + size / 14, size * 3 / 5, size * 2 / 7);
            g2.dispose();
        }
    }

    /**
     * Label whose text is painted with a blue-to-cyan gradient.
     *
     * @author Usman
     */
    private static class GradientLabel extends JLabel {

        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the label.
         *
         * @param text the text
         * @param font the font
         */
        GradientLabel(String text, java.awt.Font font) {
            super(text);
            setFont(font);
        }

        /**
         * Paints the text with a horizontal gradient.
         *
         * @param g graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(getText());
            g2.setPaint(new GradientPaint(0, 0, UIStyle.ACCENT_BRIGHT, w, 0, UIStyle.CYAN));
            g2.drawString(getText(), 0, fm.getAscent());
            g2.dispose();
        }
    }
}