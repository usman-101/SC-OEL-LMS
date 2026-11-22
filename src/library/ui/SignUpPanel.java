package library.ui;

import library.exception.LibraryException;
import library.model.Role;
import library.model.User;
import library.service.AuthService;
import library.service.LibrarySystem;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Sign-up form: Full Name, Email, Password, Confirm Password and, only when the email
 * matches the admin pattern, an Admin Access Code field. Validation is done by
 * {@link AuthService}; this panel only collects input and shows results.
 *
 * @author Usman
 */
public class SignUpPanel extends JPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Application services. */
    private final transient LibrarySystem system;
    /** Owning login window (used to switch back to sign in). */
    private final LoginFrame owner;
    /** Full name input. */
    private final JTextField nameField = UIStyle.createTextField(22);
    /** Email input. */
    private final JTextField emailField = UIStyle.createTextField(22);
    /** Password input. */
    private final JPasswordField passwordField = UIStyle.createPasswordField(22);
    /** Confirm password input. */
    private final JPasswordField confirmField = UIStyle.createPasswordField(22);
    /** Admin access code input (visible only for admin emails). */
    private final JPasswordField adminCodeField = UIStyle.createPasswordField(22);
    /** Label for the admin code. */
    private final JLabel adminCodeLabel = UIStyle.formLabel("Admin Access Code");
    /** Shows which role the typed email will get. */
    private final JLabel roleHint = UIStyle.label(" ", UIStyle.SMALL, UIStyle.MUTED);
    /** Submit button. */
    private final JButton submitButton = UIStyle.primaryButton("Create Account");

    /**
     * Creates the sign-up form.
     *
     * @param system application services
     * @param owner  the login window
     */
    public SignUpPanel(LibrarySystem system, LoginFrame owner) {
        this.system = system;
        this.owner = owner;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        LoginFrame.addLeft(this, UIStyle.label("Create account", UIStyle.TITLE, java.awt.Color.WHITE));
        LoginFrame.addLeft(this, UIStyle.label("Join the library in a few seconds", UIStyle.SMALL, UIStyle.MUTED));
        add(Box.createVerticalStrut(16));

        field("Full Name", nameField);
        field("Email", emailField);
        LoginFrame.addLeft(this, roleHint);
        add(Box.createVerticalStrut(6));
        field("Password (min 6 characters)", passwordField);
        field("Confirm Password", confirmField);
        field(adminCodeLabel, adminCodeField);
        setAdminVisible(false);

        add(Box.createVerticalStrut(6));
        submitButton.addActionListener(e -> doSignUp());
        LoginFrame.addLeft(this, LoginFrame.fullWidth(submitButton));
        add(Box.createVerticalStrut(12));
        LoginFrame.addLeft(this, UIStyle.linkLabel("Already have an account? <b><font color='#60A5FA'>Sign In</font></b>",
                () -> owner.showSignIn(null)));

        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateRole();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateRole();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateRole();
            }
        });
    }

    /**
     * Adds a labelled input row.
     *
     * @param label label text
     * @param input the input component
     */
    private void field(String label, JTextField input) {
        field(UIStyle.formLabel(label), input);
    }

    /**
     * Adds a labelled input row.
     *
     * @param label the label
     * @param input the input component
     */
    private void field(JLabel label, JTextField input) {
        LoginFrame.addLeft(this, label);
        add(Box.createVerticalStrut(4));
        LoginFrame.addLeft(this, LoginFrame.fullWidth(input));
        add(Box.createVerticalStrut(9));
    }

    /**
     * Shows or hides the admin code field and its label.
     *
     * @param visible true to show
     */
    private void setAdminVisible(boolean visible) {
        adminCodeLabel.setVisible(visible);
        adminCodeField.setVisible(visible);
        revalidate();
        repaint();
    }

    /**
     * Updates the role hint and admin field visibility as the email is typed.
     */
    private void updateRole() {
        String email = emailField.getText().trim();
        boolean admin = AuthService.isAdminEmail(email);
        setAdminVisible(admin);
        if (email.isEmpty()) {
            roleHint.setText(" ");
        } else if (admin) {
            roleHint.setText("Account type: Admin / Librarian (access code required)");
            roleHint.setForeground(UIStyle.ACCENT_BRIGHT);
        } else {
            roleHint.setText("Account type: Student");
            roleHint.setForeground(UIStyle.CYAN);
        }
    }

    /**
     * Submits the form to the AuthService, saves data and returns to sign in on success.
     */
    private void doSignUp() {
        try {
            User user = system.getAuthService().signUp(nameField.getText(), emailField.getText(),
                    new String(passwordField.getPassword()), new String(confirmField.getPassword()),
                    new String(adminCodeField.getPassword()));
            system.save();
            String extra = user.getRole() == Role.STUDENT
                    ? "\nYour library member ID is " + user.getMemberId() + "." : "";
            UIStyle.showInfo(this, "Account created successfully as " + user.getRole().getDisplayName()
                    + "." + extra + "\nYou can now sign in.");
            owner.showSignIn(user.getEmail());
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
        } catch (RuntimeException ex) {
            UIStyle.showError(this, "Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Clears all fields.
     */
    public void reset() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        adminCodeField.setText("");
        updateRole();
    }

    /**
     * Returns the submit button (used as the default button while this form is shown).
     *
     * @return the submit button
     */
    public JButton getSubmitButton() {
        return submitButton;
    }
}
