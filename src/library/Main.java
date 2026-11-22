package library;

import library.exception.LibraryException;
import library.service.LibrarySystem;
import library.ui.LoginFrame;
import library.ui.UIStyle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Paths;

/**
 * Entry point of the Library Book Management System. Sets the look and feel,
 * installs a global error handler so the GUI never crashes silently, loads (or seeds)
 * the data from the {@code data/} folder and shows the Sign In window on the Swing event thread.
 *
 * @author Usman
 */
public final class Main {

    /** Folder (relative to the working directory) where data files are stored. */
    public static final String DATA_DIR = "data";

    /**
     * Not instantiable.
     */
    private Main() {
    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    /**
     * Initialises the look and feel, data and login window. Runs on the event dispatch thread.
     */
    private static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fall back to the default cross-platform look and feel
        }
        UIStyle.applyGlobalDefaults();

        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, "An unexpected error occurred:\n" + ex,
                        "Error", JOptionPane.ERROR_MESSAGE)));

        LibrarySystem system = new LibrarySystem(Paths.get(DATA_DIR));
        try {
            system.load();
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(null, "Could not load library data:\n" + e.getMessage()
                    + "\nThe application will start with the data that could be read.",
                    "Data Error", JOptionPane.WARNING_MESSAGE);
        }
        new LoginFrame(system).setVisible(true);
    }
}
