package library.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Admin / Librarian panel: a sidebar (Dashboard, Books, Issue/Return, Members, Overdue,
 * Transactions, Logout) and a CardLayout content area showing the selected page.
 *
 * @author Usman
 */
public class AdminPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    private final MainFrame frame;
    /** Layout for the pages. */
    private final CardLayout cards = new CardLayout();
    /** Container for the pages. */
    private final JPanel content = new JPanel(cards);
    /** Pages by name, in sidebar order. */
    private final Map<String, Refreshable> pages = new LinkedHashMap<>();
    /** The sidebar. */
    private final SidebarPanel sidebar = new SidebarPanel("LIBRARIAN MENU");

    /**
     * Creates the admin panel.
     *
     * @param frame the main window
     */
    public AdminPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        content.setBackground(UIStyle.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(14, 22, 12, 22));

        addPage("\uD83C\uDFE0", "Dashboard", new DashboardPanel(frame));
        addPage("\uD83D\uDCDA", "Books", new BooksPanel(frame));
        sidebar.addBottomItem("\uD83D\uDEAA", "Logout", frame::logout);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        sidebar.selectIndex(0);
    }

    /**
     * Registers a page and its sidebar item.
     *
     * @param icon icon text
     * @param name page name
     * @param page the page panel
     * @param <P>  a panel that is refreshable
     */
    private <P extends JPanel & Refreshable> void addPage(String icon, String name, P page) {
        pages.put(name, page);
        content.add(page, name);
        sidebar.addItem(icon, name, () -> show(name));
    }

    /**
     * Shows a page after refreshing it.
     *
     * @param name page name
     */
    public void show(String name) {
        Refreshable page = pages.get(name);
        if (page != null) page.refresh();
        cards.show(content, name);
        frame.setStatus("Opened " + name + ".");
    }

    /**
     * Refreshes every page (called after any data change so the dashboard stays current).
     */
    @Override
    public void refresh() {
        for (Refreshable r : pages.values()) {
            r.refresh();
        }
    }
}
