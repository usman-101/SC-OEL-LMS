package library.ui;

import library.exception.LibraryException;
import library.model.Member;
import library.model.User;
import library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Student panel (read-only access to the catalogue): sidebar with Browse Books, My Books,
 * My History and Logout, plus a top bar showing "Welcome, name" and a summary of the
 * student's issued, reserved and overdue books.
 *
 * @author Usman
 */
public class StudentPanel extends NavigationPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Welcome text. */
    private final JLabel welcome = UIStyle.label("", UIStyle.HEADER, UIStyle.TEXT);
    /** Issued count badge text. */
    private final JLabel issuedLabel = UIStyle.label("", UIStyle.SMALL_BOLD, UIStyle.ACCENT_BRIGHT);
    /** Reserved count badge text. */
    private final JLabel reservedLabel = UIStyle.label("", UIStyle.SMALL_BOLD, UIStyle.PURPLE);
    /** Overdue count badge text. */
    private final JLabel overdueLabel = UIStyle.label("", UIStyle.SMALL_BOLD, UIStyle.DANGER);

    /**
     * Creates the student panel.
     *
     * @param frame the main window
     */
    public StudentPanel(MainFrame frame) {
        super(frame, "STUDENT MENU");
        setTopBar(buildWelcomeBar());
        addPage("\uD83D\uDD0D", "Browse Books", new BrowseBooksPanel(frame, this));
        addPage("\uD83D\uDCD6", "My Books", new MyBooksPanel(frame, this));
        addPage("\uD83D\uDCDC", "My History", new MyHistoryPanel(frame, this));
        addLogout();
        updateSummary();
    }

    /**
     * Builds the welcome / summary bar.
     *
     * @return the bar
     */
    private JPanel buildWelcomeBar() {
        JPanel bar = UIStyle.glassBar(false, true);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        bar.add(welcome, BorderLayout.WEST);
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        stats.setOpaque(false);
        stats.add(issuedLabel);
        stats.add(reservedLabel);
        stats.add(overdueLabel);
        bar.add(stats, BorderLayout.EAST);
        return bar;
    }

    /**
     * Updates the welcome text and the issued / reserved / overdue summary.
     */
    private void updateSummary() {
        User u = frame.getUser();
        welcome.setText("Welcome, " + u.getFullName() + "!");
        String mid = memberIdOrEmpty();
        LibraryService lib = frame.getSystem().getLibraryService();
        int issued = lib.getActiveLoansByMember(mid).size();
        int reserved = lib.getReservedBooksByMember(mid).size();
        int overdue = lib.getOverdueLoansByMember(mid).size();
        issuedLabel.setText("Books issued: " + issued);
        reservedLabel.setText("Reserved: " + reserved);
        overdueLabel.setText("Overdue: " + overdue);
        overdueLabel.setForeground(overdue > 0 ? UIStyle.OVERDUE_TEXT : UIStyle.MUTED);
    }

    /**
     * Returns the student's member ID, looking it up by email if the account link is missing.
     *
     * @return the member ID, or empty string if the student has no member record
     */
    String memberIdOrEmpty() {
        User u = frame.getUser();
        if (!u.getMemberId().isEmpty() && frame.getSystem().getMemberService().findById(u.getMemberId()) != null) {
            return u.getMemberId();
        }
        Member m = frame.getSystem().getMemberService().findByEmail(u.getEmail());
        return m == null ? "" : m.getMemberId();
    }

    /**
     * Returns the student's member ID or throws if the account has no member record.
     *
     * @return the member ID
     * @throws LibraryException if no member record is linked to this account
     */
    String requireMemberId() throws LibraryException {
        String mid = memberIdOrEmpty();
        if (mid.isEmpty()) {
            throw new LibraryException("Your account is not linked to a library member record. Please contact the librarian.");
        }
        return mid;
    }

    /**
     * Refreshes all pages and the summary bar.
     */
    @Override
    public void refresh() {
        super.refresh();
        updateSummary();
    }
}
