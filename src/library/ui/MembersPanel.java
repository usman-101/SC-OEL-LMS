package library.ui;

import library.exception.LibraryException;
import library.model.Member;
import library.service.LibraryService;
import library.service.MemberService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

/**
 * Member management: register members (auto-generated MEM-xxx IDs), view them in a table,
 * search by name or ID, update and delete (members with active loans cannot be deleted).
 *
 * @author Usman
 */
public class MembersPanel extends JPanel implements Refreshable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Owning window. */
    private final MainFrame frame;
    /** Table model. */
    private final DefaultTableModel model = UIStyle.createTableModel(
            new String[]{"Member ID", "Name", "Phone", "Email", "Active Loans"});
    /** Members table. */
    private final JTable table = new JTable(model);
    /** Table or empty message. */
    private final TableCard tableCard = new TableCard(table, "\uD83D\uDC65");
    /** Search input. */
    private final JTextField searchField = UIStyle.createTextField(18);
    /** Member ID (read-only). */
    private final JTextField idField = UIStyle.createTextField(14);
    /** Name input. */
    private final JTextField nameField = UIStyle.createTextField(14);
    /** Phone input. */
    private final JTextField phoneField = UIStyle.createTextField(14);
    /** Email input. */
    private final JTextField emailField = UIStyle.createTextField(14);

    /**
     * Creates the members panel.
     *
     * @param frame the main window
     */
    public MembersPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setOpaque(false);

        JPanel north = new JPanel(new BorderLayout(0, 4));
        north.setOpaque(false);
        north.add(UIStyle.pageHeader("Members", "Register and manage library members."), BorderLayout.NORTH);
        UIStyle.RoundedPanel searchCard = new UIStyle.RoundedPanel(new BorderLayout(10, 0));
        searchCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        searchCard.add(UIStyle.iconLabel("\uD83D\uDD0D", "Search by name or ID", UIStyle.SMALL_BOLD, UIStyle.MUTED),
                BorderLayout.WEST);
        searchCard.add(UIStyle.height(searchField, 32), BorderLayout.CENTER);
        north.add(searchCard, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                reloadTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                reloadTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reloadTable();
            }
        });

        UIStyle.styleTable(table);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(0).setMinWidth(82);
        table.getColumnModel().getColumn(4).setMinWidth(90);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });

        JPanel center = new JPanel(new BorderLayout(14, 0));
        center.setOpaque(false);
        center.add(tableCard, BorderLayout.CENTER);
        center.add(buildForm(), BorderLayout.EAST);
        add(center, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Builds the member form.
     *
     * @return the form card
     */
    private JPanel buildForm() {
        UIStyle.RoundedPanel card = new UIStyle.RoundedPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(290, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridy = 0;

        JButton register = UIStyle.createButton("Register", UIStyle.SUCCESS);
        JButton update = UIStyle.createButton("Update", UIStyle.ACCENT);
        JButton delete = UIStyle.createButton("Delete", UIStyle.DANGER);
        JButton clear = UIStyle.createButton("Clear", UIStyle.NEUTRAL);
        clear.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        register.addActionListener(e -> doRegister());
        update.addActionListener(e -> doUpdate());
        delete.addActionListener(e -> doDelete());
        clear.addActionListener(e -> clearForm());

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UIStyle.label("Member Details", UIStyle.HEADER, UIStyle.TEXT), BorderLayout.WEST);
        head.add(clear, BorderLayout.EAST);
        card.add(head, c);

        idField.setEditable(false);
        idField.setToolTipText("Generated automatically");
        BooksPanel.addField(card, c, "Member ID (auto)", idField);
        BooksPanel.addField(card, c, "Full Name", nameField);
        BooksPanel.addField(card, c, "Phone (optional)", phoneField);
        BooksPanel.addField(card, c, "Email", emailField);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 6, 6));
        buttons.setOpaque(false);
        for (JButton b : new JButton[]{register, update, delete}) {
            b.setBorder(BorderFactory.createEmptyBorder(9, 4, 9, 4));
        }
        buttons.add(register);
        buttons.add(update);
        buttons.add(delete);
        c.gridy++;
        c.insets = new Insets(14, 0, 0, 0);
        card.add(buttons, c);
        c.gridy++;
        c.weighty = 1;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        card.add(filler, c);
        return card;
    }

    /**
     * Reloads the table using the search text.
     */
    private void reloadTable() {
        MemberService ms = frame.getSystem().getMemberService();
        LibraryService lib = frame.getSystem().getLibraryService();
        List<Member> list = ms.search(searchField.getText());
        model.setRowCount(0);
        for (Member m : list) {
            model.addRow(new Object[]{m.getMemberId(), m.getName(), m.getPhone().isEmpty() ? "-" : m.getPhone(),
                    m.getEmail(), lib.getActiveLoansByMember(m.getMemberId()).size()});
        }
        if (ms.count() == 0) {
            tableCard.showEmpty("No members registered yet.");
        } else if (list.isEmpty()) {
            tableCard.showEmpty("No members match your search.");
        } else {
            tableCard.showTable();
        }
    }

    /**
     * Fills the form from the selected row.
     */
    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
        Member m = frame.getSystem().getMemberService().findById(id);
        if (m == null) return;
        idField.setText(m.getMemberId());
        nameField.setText(m.getName());
        phoneField.setText(m.getPhone());
        emailField.setText(m.getEmail());
    }

    /**
     * Registers a new member.
     */
    private void doRegister() {
        try {
            Member m = frame.getSystem().getMemberService().registerMember(nameField.getText(),
                    phoneField.getText(), emailField.getText());
            frame.dataChanged();
            UIStyle.showInfo(this, "Member registered with ID " + m.getMemberId() + ".");
            frame.setStatus("Member " + m.getMemberId() + " (" + m.getName() + ") registered.");
            clearForm();
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Updates the selected member.
     */
    private void doUpdate() {
        try {
            if (table.getSelectedRow() < 0 && idField.getText().trim().isEmpty()) {
                throw new LibraryException("Please select a member in the table to update.");
            }
            Member m = frame.getSystem().getMemberService().updateMember(idField.getText(), nameField.getText(),
                    phoneField.getText(), emailField.getText());
            frame.dataChanged();
            frame.setStatus("Member " + m.getMemberId() + " updated.");
        } catch (LibraryException ex) {
            UIStyle.showError(this, ex.getMessage());
            frame.setStatus(ex.getMessage(), true);
        }
    }

    /**
     * Deletes the selected member after confirmation.
     */
    private void doDelete() {
        MemberService ms = frame.getSystem().getMemberService();
        try {
            Member m = ms.requireMember(idField.getText().trim().isEmpty() ? null : idField.getText());
            if (!UIStyle.confirm(this, "Delete member " + m.getMemberId() + " (" + m.getName() + ")?",
                    "Confirm Delete")) {
                return;
            }
            ms.deleteMember(m.getMemberId());
            frame.dataChanged();
            frame.setStatus("Member " + m.getMemberId() + " deleted.");
            clearForm();
        } catch (LibraryException ex) {
            String msg = ex.getMessage().startsWith("Member ID cannot be empty")
                    ? "Please select a member in the table to delete." : ex.getMessage();
            UIStyle.showError(this, msg);
            frame.setStatus(msg, true);
        }
    }

    /**
     * Clears the form; the ID field tooltip shows the next member ID.
     */
    private void clearForm() {
        table.clearSelection();
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        idField.setToolTipText("Next ID: " + frame.getSystem().getMemberService().peekNextMemberId());
        nameField.requestFocusInWindow();
    }

    /**
     * Reloads the table.
     */
    @Override
    public void refresh() {
        reloadTable();
    }
}
