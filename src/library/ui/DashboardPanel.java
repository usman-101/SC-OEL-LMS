package library.ui;

import library.model.Transaction;
import library.service.LibraryService;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

/**
 * Admin dashboard: coloured statistic cards (Total, Available, Issued and Reserved books,
 * Total Members, Overdue count) and a table of the most recent activity.
 * Refreshed automatically after every change.
 *
 * @author Usman
 */
public class DashboardPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Number of recent transactions shown. */
    private static final int RECENT_COUNT = 10;

    /** Owning window. */
    private final MainFrame frame;
    /** Total books card. */
    private final StatCard totalCard = new StatCard("Total Books", "\uD83D\uDCDA", UIStyle.ACCENT);
    /** Available books card. */
    private final StatCard availableCard = new StatCard("Available Books", "\u2714", UIStyle.SUCCESS);
    /** Issued books card. */
    private final StatCard issuedCard = new StatCard("Issued Books", "\uD83D\uDCD6", UIStyle.CYAN);
    /** Reserved books card. */
    private final StatCard reservedCard = new StatCard("Reserved Books", "\uD83D\uDD16", UIStyle.WARNING);
    /** Members card. */
    private final StatCard membersCard = new StatCard("Total Members", "\uD83D\uDC65", UIStyle.PURPLE);
    /** Overdue card. */
    private final StatCard overdueCard = new StatCard("Overdue Books", "\u23F0", UIStyle.DANGER);
    /** Recent activity table model. */
    private final DefaultTableModel recentModel =
            UIStyle.createTableModel(new String[]{"Date/Time", "Type", "Book ID", "Member ID", "Details"});

    /**
     * Creates the dashboard.
     *
     * @param frame the main window
     */
    public DashboardPanel(MainFrame frame) {
        super(new BorderLayout(0, 16));
        this.frame = frame;
        setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIStyle.pageHeader("Dashboard", "Live overview of the library. Numbers update after every change."),
                BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.add(totalCard);
        grid.add(availableCard);
        grid.add(issuedCard);
        grid.add(reservedCard);
        grid.add(membersCard);
        grid.add(overdueCard);
        top.add(grid, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        UIStyle.RoundedPanel recentCard = new UIStyle.RoundedPanel(new BorderLayout(0, 10));
        recentCard.add(UIStyle.label("Recent Activity", UIStyle.HEADER, UIStyle.TEXT), BorderLayout.NORTH);
        JTable recent = new JTable(recentModel);
        UIStyle.styleTable(recent);
        recent.getColumnModel().getColumn(4).setPreferredWidth(320);
        recentCard.add(UIStyle.scroll(recent), BorderLayout.CENTER);
        add(recentCard, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Reloads statistics and recent activity.
     */
    @Override
    public void refresh() {
        Map<String, Integer> s = frame.getSystem().getLibraryService().getStatistics();
        totalCard.setValue(s.get(LibraryService.STAT_TOTAL));
        availableCard.setValue(s.get(LibraryService.STAT_AVAILABLE));
        issuedCard.setValue(s.get(LibraryService.STAT_ISSUED));
        reservedCard.setValue(s.get(LibraryService.STAT_RESERVED));
        membersCard.setValue(s.get(LibraryService.STAT_MEMBERS));
        overdueCard.setValue(s.get(LibraryService.STAT_OVERDUE));

        recentModel.setRowCount(0);
        List<Transaction> all = frame.getSystem().getTransactionService().getAll();
        for (int i = 0; i < Math.min(RECENT_COUNT, all.size()); i++) {
            Transaction t = all.get(i);
            recentModel.addRow(new Object[]{t.getDateTime(), t.getType().getDisplayName(), t.getBookId(),
                    t.getMemberId(), t.getDetails()});
        }
    }
}
