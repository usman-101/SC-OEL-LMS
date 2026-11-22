package library.ui;

import library.exception.LibraryException;
import library.model.Role;
import library.model.User;
import library.service.LibrarySystem;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main application window shown after login. It has a bold header bar (app title,
 * logged-in user and role badge), the role-specific panel in the centre and a status
 * bar at the bottom for feedback messages.
 *
 * @author Usman
 */
public class MainFrame extends JFrame {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Application services. */
    private final transient LibrarySystem system;
    /** The logged-in user. */
    private final transient User user;
    /** Status bar label. */
    private final JLabel statusLabel = UIStyle.label("Ready", UIStyle.SMALL, UIStyle.MUTED);
    /** Timer that resets the status colour after a while. */
    private Timer statusTimer;
    /** The role-specific content panel. */
    private JPanel rolePanel;

    /**
     * Creates the main window for the given user.
     *
     * @param system application services
     * @param user   the logged-in user
     */
    public MainFrame(LibrarySystem system, User user) {
        super("Library Book Management System - " + user.getRole().getDisplayName() + " Panel");
        this.system = system;
        this.user = user;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(UIStyle.MIN_WINDOW);
        setSize(1240, 760);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BACKGROUND);
        root.add(buildHeader(), BorderLayout.NORTH);
        rolePanel = createRolePanel();
        root.add(rolePanel, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);
        setStatus("Welcome, " + user.getFullName() + "! Signed in as " + user.getRole().getDisplayName() + ".", false);
    }

    /**
     * Creates the panel for the user's role.
     *
     * @return the role panel
     */
    private JPanel createRolePanel() {
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(UIStyle.BACKGROUND);
        placeholder.add(UIStyle.label(user.getRole().getDisplayName() + " panel", UIStyle.HEADER, UIStyle.MUTED));
        return placeholder;
    }

    /**
     * Builds the header bar with the app title, user name and role badge.
     *
     * @return the header
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(UIStyle.label("📚", new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 22), UIStyle.NAVY));
        left.add(UIStyle.label("Library Book Management System", UIStyle.HEADER, UIStyle.NAVY));
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(UIStyle.label(user.getFullName(), UIStyle.BODY_BOLD, UIStyle.TEXT));
        right.add(UIStyle.label(user.getEmail(), UIStyle.SMALL, UIStyle.MUTED));
        right.add(UIStyle.badge(user.getRole() == Role.ADMIN ? "ADMIN / LIBRARIAN" : "STUDENT",
                user.getRole() == Role.ADMIN ? UIStyle.ACCENT : UIStyle.SUCCESS));
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Builds the bottom status bar.
     *
     * @return the status bar
     */
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        bar.add(statusLabel, BorderLayout.WEST);
        JLabel version = UIStyle.label("Data folder: " + (system.getDataStore() == null ? "in-memory"
                : system.getDataStore().getDataDir().toAbsolutePath()), UIStyle.SMALL, UIStyle.MUTED);
        bar.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
        bar.add(version, BorderLayout.EAST);
        return bar;
    }

    /**
     * Shows a feedback message in the status bar.
     *
     * @param message the message
     * @param error   true to show it in red
     */
    public void setStatus(String message, boolean error) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        statusLabel.setText("[" + time + "]  " + message);
        statusLabel.setForeground(error ? UIStyle.DANGER : UIStyle.SUCCESS.darker());
        if (statusTimer != null) statusTimer.stop();
        statusTimer = new Timer(6000, e -> statusLabel.setForeground(UIStyle.MUTED));
        statusTimer.setRepeats(false);
        statusTimer.start();
    }

    /**
     * Shows a success message in the status bar.
     *
     * @param message the message
     */
    public void setStatus(String message) {
        setStatus(message, false);
    }

    /**
     * Saves all data after a change and refreshes every view. Errors while saving are shown to the user.
     */
    public void dataChanged() {
        try {
            system.save();
        } catch (LibraryException ex) {
            UIStyle.showError(this, "Changes could not be saved: " + ex.getMessage());
            setStatus("Save failed: " + ex.getMessage(), true);
        }
        if (rolePanel instanceof Refreshable) {
            ((Refreshable) rolePanel).refresh();
        }
    }

    /**
     * Logs out: closes this window and returns to the Sign In screen.
     */
    public void logout() {
        if (!UIStyle.confirm(this, "Are you sure you want to log out?", "Logout")) return;
        system.getLibraryService().setActor(null);
        dispose();
        LoginFrame login = new LoginFrame(system);
        login.showSignIn(user.getEmail());
        login.setVisible(true);
    }

    /**
     * Returns the application services.
     *
     * @return the library system
     */
    public LibrarySystem getSystem() {
        return system;
    }

    /**
     * Returns the logged-in user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
