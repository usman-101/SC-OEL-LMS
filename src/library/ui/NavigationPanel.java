package library.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for role panels: a {@link SidebarPanel} on the left and a CardLayout content
 * area on the right. Subclasses register pages with {@link #addPage}; selecting a sidebar
 * item refreshes and shows the page. A Logout item is added at the bottom.
 *
 * @author Usman
 */
public abstract class NavigationPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    protected final MainFrame frame;
    /** Layout for the pages. */
    private final CardLayout cards = new CardLayout();
    /** Container for the pages. */
    protected final JPanel content = new JPanel(cards);
    /** Pages by name, in sidebar order. */
    private final transient Map<String, Refreshable> pages = new LinkedHashMap<>();
    /** Sidebar buttons by page name. */
    private final transient Map<String, JButton> navButtons = new LinkedHashMap<>();
    /** The sidebar. */
    private final SidebarPanel sidebar;

    /**
     * Creates the navigation layout.
     *
     * @param frame        the main window
     * @param sidebarTitle title shown at the top of the sidebar
     */
    protected NavigationPanel(MainFrame frame, String sidebarTitle) {
        super(new BorderLayout());
        this.frame = frame;
        this.sidebar = new SidebarPanel(sidebarTitle);
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(14, 22, 12, 22));
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    /**
     * Registers a page and its sidebar item.
     *
     * @param icon icon text
     * @param name page name (also the sidebar text)
     * @param page the page panel
     * @param <P>  a refreshable panel
     */
    protected <P extends JPanel & Refreshable> void addPage(String icon, String name, P page) {
        pages.put(name, page);
        content.add(page, name);
        navButtons.put(name, sidebar.addItem(icon, name, () -> show(name)));
        if (pages.size() == 1) sidebar.selectIndex(0);
    }

    /**
     * Places a bar above the page content (used by the student panel for its welcome summary).
     *
     * @param bar the component to show above the pages
     */
    protected void setTopBar(javax.swing.JComponent bar) {
        remove(content);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(bar, BorderLayout.NORTH);
        wrap.add(content, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }

    /**
     * Adds the Logout item at the bottom of the sidebar.
     */
    protected void addLogout() {
        sidebar.addBottomItem("\uD83D\uDEAA", "Logout", frame::logout);
    }

    /**
     * Refreshes and shows a page, and highlights its sidebar item.
     *
     * @param name the page name
     */
    public void show(String name) {
        Refreshable page = pages.get(name);
        if (page == null) return;
        page.refresh();
        cards.show(content, name);
        JButton b = navButtons.get(name);
        if (b != null) sidebar.select(b);
        frame.setStatus("Opened " + name + ".");
    }

    /**
     * Refreshes every page (called after any data change so all views stay current).
     */
    @Override
    public void refresh() {
        for (Refreshable r : pages.values()) {
            r.refresh();
        }
    }
}
