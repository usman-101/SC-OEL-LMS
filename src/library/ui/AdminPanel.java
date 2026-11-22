package library.ui;

/**
 * Admin / Librarian panel: a sidebar (Dashboard, Books, Issue/Return, Members, Overdue,
 * Transactions, Logout) and a CardLayout content area showing the selected page.
 *
 * @author Usman
 */
public class AdminPanel extends NavigationPanel {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the admin panel with all librarian pages.
     *
     * @param frame the main window
     */
    public AdminPanel(MainFrame frame) {
        super(frame, "LIBRARIAN MENU");
        addPage("\uD83C\uDFE0", "Dashboard", new DashboardPanel(frame));
        addPage("\uD83D\uDCDA", "Books", new BooksPanel(frame));
        addPage("\uD83D\uDD04", "Issue/Return", new IssueReturnPanel(frame));
        addPage("\uD83D\uDC65", "Members", new MembersPanel(frame));
        addPage("\u23F0", "Overdue", new OverduePanel(frame));
        addPage("\uD83D\uDCDC", "Transactions", new TransactionsPanel(frame));
        addLogout();
    }
}
