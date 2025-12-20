package GymSystem.AdminSys;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.ArrayList;
import GymSystem.*;
import GymSystem.Account.SRole;
import java.io.*;
import java.nio.file.*;


class AdminAccountsPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;

    private Color darkBg = new Color(20, 22, 26);
    private Color sidebarNav = new Color(30, 34, 40);
    private Color accentBlue = new Color(0, 150, 255);
    private Color dangerRed = new Color(220, 53, 69);
    private Color textLight = new Color(220, 220, 225);

    public AdminAccountsPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        setBackground(darkBg);

        model = new DefaultTableModel(new Object[]{"ID", "Username", "Role", "Name", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tbl = new JTable(model);
        styleDarkTable(tbl);
        refresh();

        JPanel controlConsole = new JPanel(new BorderLayout());
        controlConsole.setBackground(sidebarNav);
        controlConsole.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 55, 65)));
        controlConsole.setPreferredSize(new Dimension(0, 70));

        JLabel lblTitle = new JLabel("  ACCOUNTS MANAGEMENT");
        lblTitle.setForeground(accentBlue);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlConsole.add(lblTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        btnPanel.setOpaque(false);

        JButton btnAdd = createConsoleButton("ADD USER", accentBlue);
        JButton btnEdit = createConsoleButton("EDIT", new Color(100, 100, 110));
        JButton btnSearch = createConsoleButton("SEARCH", new Color(100, 100, 110));
        JButton btnRefresh = createConsoleButton("REFRESH", new Color(100, 100, 110));
        JButton btnDelete = createConsoleButton("DELETE", dangerRed);

        btnAdd.addActionListener(e -> addAccount());
        btnEdit.addActionListener(e -> editAccount());
        btnDelete.addActionListener(e -> deleteAccount());
        btnSearch.addActionListener(e -> searchAccount());
        btnRefresh.addActionListener(e -> refresh());

        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnAdd);

        controlConsole.add(btnPanel, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scroll.getViewport().setBackground(darkBg);
        scroll.setBackground(darkBg);

        add(controlConsole, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void styleDarkTable(JTable table) {
        table.setBackground(sidebarNav);
        table.setForeground(textLight);
        table.setGridColor(new Color(45, 48, 55));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(accentBlue.getRed(), accentBlue.getGreen(), accentBlue.getBlue(), 50));
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(15, 17, 20));
        header.setForeground(accentBlue);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentBlue));
    }

    private JButton createConsoleButton(String text, Color borderColor) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(40, 44, 52));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> accounts = Database.readAccounts();
        for (ArrayList<String> u : accounts) {
            if (u.size() >= 7) {
                model.addRow(new Object[]{ u.get(0), u.get(4), u.get(3), u.get(1), u.get(5), u.get(6) });
            }
        }
    }

    void addAccount() {
        UserFormDialog d = new UserFormDialog(null);
        d.setVisible(true);
        if (d.saved) {
            parent.me.addAccount(-1, d.username, d.password, d.role, d.name, d.email, d.phone , d.extra);
            refresh();
        }
    }

    void editAccount() {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an account to edit"); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        ArrayList<String> record = Database.readAccounts().stream()
                .filter(a -> Integer.parseInt(a.get(0)) == id).findFirst().orElse(null);
        if (record == null) return;
        Account acc = new Account(Integer.parseInt(record.get(0)), record.get(1), record.get(2),
                SRole.valueOf(record.get(3)), record.get(4), record.get(5), record.get(6));
        UserFormDialog d = new UserFormDialog(acc);
        d.setVisible(true);
        if (d.saved) {
            parent.me.editAccount(acc.getId(), d.username, d.password, d.role, d.name, d.email, d.phone);
            refresh();
        }
    }

    void deleteAccount() {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an account to delete"); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        SRole role = SRole.valueOf(model.getValueAt(row, 2).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete account?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try { parent.me.deleteAccountCascade(id, role); parent.refreshAllTabs(); } catch (Exception ex) { }
    }

    void searchAccount() {
        String keyword = JOptionPane.showInputDialog(this, "Search keyword:");
        if (keyword == null || keyword.isEmpty()) { refresh(); return; }
        keyword = keyword.trim().toLowerCase();
        model.setRowCount(0);
        for (ArrayList<String> u : Database.readAccounts()) {
            String finalKeyword = keyword;
            if (u.stream().anyMatch(f -> f.toLowerCase().contains(finalKeyword))) {
                model.addRow(new Object[]{ u.get(0), u.get(4), u.get(3), u.get(1), u.get(5), u.get(6) });
            }
        }
    }
}
class UserFormDialog extends JDialog {
    JTextField txtUser, txtName, txtEmail, txtPhone, txtExtra;
    JPasswordField txtPass;
    JComboBox<String> cbRole;
    boolean saved = false;
    String username, password, name, email, phone, extra = "";
    SRole role;

    public UserFormDialog(Account existing) {
        setModal(true);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setUndecorated(true);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(30, 34, 40));
        container.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 255), 2));

        JLabel lblHead = new JLabel("  USER CONFIGURATION", SwingConstants.LEFT);
        lblHead.setPreferredSize(new Dimension(0, 40));
        lblHead.setOpaque(true);
        lblHead.setBackground(new Color(0, 150, 255));
        lblHead.setForeground(Color.WHITE);
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 12));
        container.add(lblHead, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(7, 2, 10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtUser = createDarkField(); txtPass = new JPasswordField();
        txtName = createDarkField(); txtEmail = createDarkField();
        txtPhone = createDarkField(); txtExtra = createDarkField();
        cbRole = new JComboBox<>(new String[]{"USER", "MEMBER", "COACH", "ADMIN"});

        styleComp(p, "Username", txtUser);
        styleComp(p, "Password", txtPass);
        styleComp(p, "Role", cbRole);
        styleComp(p, "Full Name", txtName);
        styleComp(p, "Email", txtEmail);
        styleComp(p, "Phone", txtPhone);
        styleComp(p, "Specialty", txtExtra);

        container.add(p, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton ok = new JButton("SAVE CHANGES");
        JButton cancel = new JButton("DISCARD");

        ok.addActionListener(e -> onSave());
        cancel.addActionListener(e -> { saved = false; dispose(); });

        btnPanel.add(cancel); btnPanel.add(ok);
        container.add(btnPanel, BorderLayout.SOUTH);

        add(container);
        if (existing != null) {
            txtUser.setText(existing.getUsername());
            txtPass.setText(existing.getPassword());
            txtName.setText(existing.getName());
            txtEmail.setText(existing.getEmail());
            txtPhone.setText(existing.getPhone());
            cbRole.setSelectedItem(existing.getRole().name());
        }
    }

    private JTextField createDarkField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(40, 44, 52));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 75)));
        return f;
    }

    private void styleComp(JPanel p, String text, JComponent comp) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 190));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        p.add(l); p.add(comp);
    }

    void onSave() {
        username = txtUser.getText().trim();
        password = new String(txtPass.getPassword());
        name = txtName.getText().trim();
        email = txtEmail.getText().trim();
        phone = txtPhone.getText().trim();
        extra = txtExtra.getText().trim();
        role = SRole.valueOf((String)cbRole.getSelectedItem());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required"); return;
        }
        saved = true;
        dispose();
    }
}

class AdminMembersPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;


    private Color darkBg = new Color(18, 20, 24);
    private Color consoleGray = new Color(32, 36, 42);
    private Color accentCyan = new Color(0, 200, 255);
    private Color textSilver = new Color(200, 205, 210);

    public AdminMembersPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        setBackground(darkBg);

        model = new DefaultTableModel(new Object[]{"ID", "Account ID", "Member Name", "Subscription End", "Assigned Coach"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tbl = new JTable(model);
        styleAdminTable(tbl);
        refresh();

        JPanel controlConsole = new JPanel(new BorderLayout());
        controlConsole.setBackground(consoleGray);
        controlConsole.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 65, 75)));
        controlConsole.setPreferredSize(new Dimension(0, 75));

        JLabel lblTitle = new JLabel("   MEMBERSHIP DIRECTORY");
        lblTitle.setForeground(accentCyan);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlConsole.add(lblTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        btnPanel.setOpaque(false);

        JButton btnAdd = createAdminButton("NEW MEMBER", accentCyan);
        JButton btnEdit = createAdminButton("EDIT", new Color(120, 130, 140));
        JButton btnAssign = createAdminButton("ASSIGN COACH", new Color(155, 89, 182));
        JButton btnSearch = createAdminButton("SEARCH", new Color(120, 130, 140));
        JButton btnRefresh = createAdminButton("REFRESH", new Color(120, 130, 140));
        JButton btnDelete = createAdminButton("DELETE", new Color(231, 76, 60));

        btnAdd.addActionListener(e -> addMember());
        btnEdit.addActionListener(e -> editMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnSearch.addActionListener(e -> searchMember());
        btnAssign.addActionListener(e -> assignCoach());
        btnRefresh.addActionListener(e -> refresh());

        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnAssign);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnAdd);

        controlConsole.add(btnPanel, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scroll.getViewport().setBackground(darkBg);
        scroll.setBackground(darkBg);

        add(controlConsole, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void styleAdminTable(JTable table) {
        table.setBackground(consoleGray);
        table.setForeground(textSilver);
        table.setGridColor(new Color(50, 55, 60));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(0, 200, 255, 40));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(25, 28, 32));
        header.setForeground(accentCyan);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentCyan));
    }

    private JButton createAdminButton(String text, Color highlight) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(45, 50, 58));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(highlight, 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }


    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> members = Database.readMembers();
        for (ArrayList<String> u : members) {
            model.addRow(new Object[]{ u.get(0), u.get(1), u.get(2), u.get(3), u.get(4) });
        }
    }

    void addMember() {
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtCoachId = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Member Name:")); panel.add(txtName);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Coach ID (0=none):")); panel.add(txtCoachId);

        int result = JOptionPane.showConfirmDialog(this, panel, "SYSTEM: Add New Member", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int coachId = Integer.parseInt(txtCoachId.getText().trim());
            if (coachId != 0 && !Database.checkIfIdExistsInFile(coachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "Coach ID not found."); return;
            }
            if (txtName.getText().trim().isEmpty()) return;

            parent.me.addMemberExtended(-1, -1, txtName.getText().trim(), txtEmail.getText().trim(), txtPhone.getText().trim(), coachId);
            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Record Initialized.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data Sync Error."); }
    }

    void editMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select row."); return; }
        int mid = Integer.parseInt(model.getValueAt(row, 0).toString());
        int aid = Integer.parseInt(model.getValueAt(row, 1).toString());
        String name = model.getValueAt(row, 2).toString();
        String date = model.getValueAt(row, 3).toString();
        String currentCoach = model.getValueAt(row, 4).toString();

        String input = JOptionPane.showInputDialog(this, "Modify Coach ID:", currentCoach);
        if (input == null) return;

        try {
            int newCoachId = Integer.parseInt(input.trim());
            if (newCoachId != 0 && !Database.checkIfIdExistsInFile(newCoachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "Coach ID invalid."); return;
            }
            Database.updateMember(mid, aid, name, date, newCoachId);
            parent.refreshAllTabs();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Update failed."); }
    }

    void deleteMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) return;
        int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int accId = Integer.parseInt(model.getValueAt(row, 1).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete membership and login account?", "Critical Action", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            parent.me.deleteMemberAndAccount(memberId, accId);
            parent.refreshAllTabs();
        }
    }

    void searchMember() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Query:");
        if (keyword == null || keyword.isEmpty()) { refresh(); return; }
        keyword = keyword.trim().toLowerCase();
        model.setRowCount(0);
        for (ArrayList<String> m : Database.readMembers()) {
            String finalKeyword = keyword;
            if (m.stream().anyMatch(f -> f.toLowerCase().contains(finalKeyword))) {
                model.addRow(new Object[]{ m.get(0), m.get(1), m.get(2), m.get(3), m.get(4) });
            }
        }
    }

    void assignCoach() {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select member first."); return; }
        String input = JOptionPane.showInputDialog(this, "Assign New Coach (ID):");
        if (input == null || input.isEmpty()) return;
        try {
            int coachId = Integer.parseInt(input.trim());
            if (!Database.checkIfIdExistsInFile(coachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "Coach not found."); return;
            }
            int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
            int accId = Integer.parseInt(model.getValueAt(row, 1).toString());
            Database.updateMember(memberId, accId, model.getValueAt(row, 2).toString(), model.getValueAt(row, 3).toString(), coachId);
            parent.refreshAllTabs();
        } catch (Exception e) { }
    }
}

class AdminCoachesPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;

    private Color darkBg = new Color(15, 18, 22);
    private Color consoleGray = new Color(28, 32, 38);
    private Color accentEmerald = new Color(46, 204, 113);
    private Color textSilver = new Color(210, 215, 220);

    public AdminCoachesPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        setBackground(darkBg);

        model = new DefaultTableModel(new Object[]{"ID", "Account ID", "Coach Name", "Professional Specialty"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tbl = new JTable(model);
        styleCoachTable(tbl);
        refresh();

        JPanel controlConsole = new JPanel(new BorderLayout());
        controlConsole.setBackground(consoleGray);
        controlConsole.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 60, 70)));
        controlConsole.setPreferredSize(new Dimension(0, 75));

        JLabel lblTitle = new JLabel("   STAFF & COACHING ROSTER");
        lblTitle.setForeground(accentEmerald);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlConsole.add(lblTitle, BorderLayout.WEST);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        btnP.setOpaque(false);

        JButton btnAdd = createStaffButton("ADD COACH", accentEmerald);
        JButton btnEdit = createStaffButton("EDIT", new Color(110, 120, 130));
        JButton btnSearch = createStaffButton("SEARCH", new Color(110, 120, 130));
        JButton btnRefresh = createStaffButton("REFRESH", new Color(110, 120, 130));
        JButton btnDelete = createStaffButton("TERMINATE", new Color(231, 76, 60));

        btnAdd.addActionListener(e -> addCoach());
        btnEdit.addActionListener(e -> editCoach());
        btnDelete.addActionListener(e -> deleteCoach());
        btnSearch.addActionListener(e -> searchCoach());
        btnRefresh.addActionListener(e -> refresh());

        btnP.add(btnSearch);
        btnP.add(btnRefresh);
        btnP.add(btnEdit);
        btnP.add(btnDelete);
        btnP.add(btnAdd);

        controlConsole.add(btnP, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scroll.getViewport().setBackground(darkBg);
        scroll.setBackground(darkBg);

        add(controlConsole, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void styleCoachTable(JTable table) {
        table.setBackground(consoleGray);
        table.setForeground(textSilver);
        table.setGridColor(new Color(45, 50, 55));
        table.setRowHeight(42);
        table.setSelectionBackground(new Color(46, 204, 113, 35));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(20, 24, 28));
        header.setForeground(accentEmerald);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentEmerald));
    }

    private JButton createStaffButton(String text, Color highlight) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(40, 45, 52));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(highlight, 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }


    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> coaches = Database.readCoachs();
        for (ArrayList<String> c : coaches) {
            if (c.size() >= 4) {
                model.addRow(new Object[]{ c.get(0), c.get(1), c.get(2), c.get(3) });
            }
        }
    }

    void addCoach() {
        JTextField txtName = new JTextField();
        JTextField txtSpecialty = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Full Name:")); panel.add(txtName);
        panel.add(new JLabel("Specialty:")); panel.add(txtSpecialty);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);

        int result = JOptionPane.showConfirmDialog(this, panel, "SYSTEM: Add New Coach", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String specialty = txtSpecialty.getText().trim();
            if (name.isEmpty() || specialty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields required."); return;
            }
            parent.me.addCoachExtended(-1, -1, name, specialty, txtEmail.getText().trim(), txtPhone.getText().trim());
            refresh();
        }
    }

    void editCoach() {
        int row = tbl.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select row."); return; }
        int coachId = Integer.parseInt(model.getValueAt(row, 0).toString());
        ArrayList<ArrayList<String>> all = Database.readCoachs();
        ArrayList<String> record = all.stream().filter(c -> Integer.parseInt(c.get(0)) == coachId).findFirst().orElse(null);
        if (record == null) return;

        JTextField txtName = new JTextField(record.get(2));
        JTextField txtSpecialty = new JTextField(record.get(3));
        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        panel.add(new JLabel("Full Name:")); panel.add(txtName);
        panel.add(new JLabel("Specialty:")); panel.add(txtSpecialty);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modify Staff Profile", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && !txtName.getText().trim().isEmpty()) {
            parent.me.editCoach(coachId, Integer.parseInt(record.get(1)), txtName.getText().trim(), txtSpecialty.getText().trim());
            refresh();
        }
    }

    void deleteCoach() {
        int row = tbl.getSelectedRow();
        if (row < 0) return;
        int coachId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int accId = Integer.parseInt(model.getValueAt(row, 1).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Coach Account?", "Confirm Termination", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            parent.me.deleteCoachAndAccount(coachId, accId);
            parent.refreshAllTabs();
        }
    }

    void searchCoach() {
        String keyword = JOptionPane.showInputDialog(this, "Search Records:");
        if (keyword == null || keyword.isEmpty()) { refresh(); return; }
        keyword = keyword.trim().toLowerCase();
        model.setRowCount(0);
        for (ArrayList<String> c : Database.readCoachs()) {
            String finalKeyword = keyword;
            if (c.stream().anyMatch(f -> f.toLowerCase().contains(finalKeyword))) {
                model.addRow(new Object[]{ c.get(0), c.get(1), c.get(2), c.get(3) });
            }
        }
    }
}

class AdminBillingPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;

    private Color darkBg = new Color(15, 15, 18);
    private Color consoleGray = new Color(25, 28, 32);
    private Color accentGold = new Color(255, 193, 7);
    private Color textSilver = new Color(210, 215, 220);

    public AdminBillingPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        setBackground(darkBg);

        model = new DefaultTableModel(new Object[]{"TXN ID", "Member ID", "Amount ($)", "Timestamp", "Transaction Note"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tbl = new JTable(model);
        styleBillingTable(tbl);
        refresh();

        JPanel controlConsole = new JPanel(new BorderLayout());
        controlConsole.setBackground(consoleGray);
        controlConsole.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 55)));
        controlConsole.setPreferredSize(new Dimension(0, 75));

        JLabel lblTitle = new JLabel("   FINANCIAL LEDGER & BILLING");
        lblTitle.setForeground(accentGold);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlConsole.add(lblTitle, BorderLayout.WEST);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        top.setOpaque(false);

        JButton btnAdd = createBillingButton("ADD INVOICE", accentGold);
        JButton btnRefresh = createBillingButton("REFRESH", new Color(110, 110, 120));
        JButton btnDelete = createBillingButton("VOID TRANSACTION", new Color(231, 76, 60));

        btnAdd.addActionListener(e -> addBilling());
        btnDelete.addActionListener(e -> deleteBilling());
        btnRefresh.addActionListener(e -> refresh());

        top.add(btnRefresh);
        top.add(btnDelete);
        top.add(btnAdd);

        controlConsole.add(top, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scroll.getViewport().setBackground(darkBg);
        scroll.setBackground(darkBg);

        add(controlConsole, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void styleBillingTable(JTable table) {
        table.setBackground(consoleGray);
        table.setForeground(textSilver);
        table.setGridColor(new Color(40, 40, 45));
        table.setRowHeight(42);
        table.setSelectionBackground(new Color(255, 193, 7, 30));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(18, 18, 22));
        header.setForeground(accentGold);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentGold));
    }

    private JButton createBillingButton(String text, Color highlight) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(35, 38, 45));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(highlight, 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> billList = Database.readBillings();
        for (ArrayList<String> b : billList) {
            model.addRow(new Object[]{ b.get(0), b.get(1), b.get(2), b.get(3), b.get(4) });
        }
    }

    void addBilling() {
        JTextField txtMemberId = new JTextField();
        JTextField txtAmount = new JTextField();
        JTextField txtDate = new JTextField(java.time.LocalDate.now().toString());
        JTextField txtNote = new JTextField();

        Object[] form = {
                "Member Account ID:", txtMemberId,
                "Amount ($):", txtAmount,
                "Date (YYYY-MM-DD):", txtDate,
                "Transaction Note:", txtNote
        };

        int result = JOptionPane.showConfirmDialog(this, form, "GENERATE BILLING RECORD", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int memberId = Integer.parseInt(txtMemberId.getText().trim());
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String date = txtDate.getText().trim();
            String note = txtNote.getText().trim();

            if (!Database.checkIfIdExistsInFile(memberId, "members.csv")) {
                JOptionPane.showMessageDialog(this, "Verification Failed: Member ID " + memberId + " not found.");
                return;
            }
            if(note.isEmpty()) note = "-";

            parent.me.addBilling(-1, memberId, amount, date, note);
            refresh();
            JOptionPane.showMessageDialog(this, "Transaction Logged Successfully.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Data Error: Numeric ID and Amount required.");
        }
    }

    void deleteBilling() {
        int row = tbl.getSelectedRow();
        if (row < 0) return;
        int billingId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voiding this transaction is permanent. Proceed?",
                "Financial Audit Warning", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            parent.me.deleteBilling(billingId);
            refresh();
        }
    }
}

class ReportFrame extends JFrame {
    AdminPanel parent;
    JTextArea textArea;

    private Color terminalBg = new Color(10, 12, 15);
    private Color textGreen = new Color(50, 255, 150);
    private Color headerGray = new Color(30, 33, 37);
    private Color accentBlue = new Color(0, 150, 255);

    public ReportFrame(String defaultText) {
        setTitle("SYSTEM REPORT GENERATOR");
        this.setSize(600, 500);
        this.getContentPane().setBackground(terminalBg);

        textArea = new JTextArea();
        textArea.setBackground(terminalBg);
        textArea.setForeground(textGreen);
        textArea.setCaretColor(Color.WHITE);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(20, 20, 20, 20));
        textArea.setText(defaultText);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 60)));
        scrollPane.getVerticalScrollBar().setBackground(terminalBg);

        JLabel lblHeader = new JLabel("  DOCUMENT PREVIEW");
        lblHeader.setOpaque(true);
        lblHeader.setBackground(headerGray);
        lblHeader.setForeground(accentBlue);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHeader.setPreferredSize(new Dimension(0, 35));

        JButton saveButton = new JButton("EXPORT TO TEXT FILE (.TXT)");
        styleReportButton(saveButton);
        saveButton.addActionListener(e -> saveReportToFile(textArea));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(lblHeader, BorderLayout.NORTH);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(saveButton, BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void styleReportButton(JButton b) {
        b.setPreferredSize(new Dimension(0, 50));
        b.setBackground(accentBlue);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void saveReportToFile(JTextArea textArea) {
        String reportContent = textArea.getText();
        if (reportContent.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "The report is empty. Please write some content before saving.",
                    "Cannot Save Empty Report",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".txt")) {
                fileToSave = new File(path + ".txt");
            }

            try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                fileWriter.write(reportContent);
                JOptionPane.showMessageDialog(
                        this,
                        "Report successfully saved to:\n" + fileToSave.getAbsolutePath(),
                        "Save Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "An error occurred while saving the file:\n" + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}

class AdminReportsPanel extends JPanel {
    AdminPanel parent;

    private Color darkBg = new Color(15, 18, 22);
    private Color cardColor = new Color(28, 32, 38);
    private Color accentBlue = new Color(0, 150, 255);
    private Color accentPurple = new Color(155, 89, 182);

    public AdminReportsPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        setBackground(darkBg);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        headerPanel.setPreferredSize(new Dimension(0, 75));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 65, 75)));

        JLabel lblTitle = new JLabel("   REPORTS & DATA OPERATIONS");
        lblTitle.setForeground(accentPurple);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel gridContainer = new JPanel(new GridLayout(2, 2, 20, 20));
        gridContainer.setOpaque(false);
        gridContainer.setBorder(new EmptyBorder(40, 40, 40, 40));

        JButton btnMembersCSV = createActionCard("IMPORT REPORT", "Load external .txt data", accentBlue);
        JButton btnExpiring = createActionCard("GENERATE REPORT", "Initialize a new system report", accentPurple);

        btnMembersCSV.addActionListener(e -> importReportTXT());
        btnExpiring.addActionListener(e -> makeReport());

        gridContainer.add(btnMembersCSV);
        gridContainer.add(btnExpiring);
        gridContainer.add(createPlaceholderCard());
        gridContainer.add(createPlaceholderCard());

        add(headerPanel, BorderLayout.NORTH);
        add(gridContainer, BorderLayout.CENTER);
    }

    private JButton createActionCard(String title, String subtitle, Color accent) {
        JButton b = new JButton("<html><div style='text-align: center;'><span style='font-size: 14px; font-weight: bold; color: white;'>" + title + "</span><br><span style='font-size: 10px; color: #AAAAAA;'>" + subtitle + "</span></div></html>");
        b.setBackground(cardColor);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 55, 65), 1),
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel createPlaceholderCard() {
        JPanel p = new JPanel();
        p.setBackground(new Color(22, 25, 30));
        p.setBorder(BorderFactory.createDashedBorder(new Color(40, 45, 50), 5, 5));
        return p;
    }
    void importReportTXT() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a .txt file to load");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                String fileContent = Files.readString(Paths.get(fileToLoad.getAbsolutePath()));
                ReportFrame rf = new ReportFrame(fileContent);
                rf.setTitle("Editing: " + fileToLoad.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "An error occurred while reading the file.", "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void makeReport() {
        new ReportFrame("");
    }
}

public class AdminPanel extends JFrame {
    JTabbedPane tabs;
    Admin me;
    AdminAccountsPanel accountsTab;
    AdminMembersPanel membersTab;
    AdminCoachesPanel coachesTab;
    AdminBillingPanel billingTab;

    private Color headerDark = new Color(10, 12, 16);
    private Color tabBg = new Color(25, 28, 35);
    private Color accentBlue = new Color(0, 150, 255);
    private Color textDim = new Color(160, 165, 175);

    public AdminPanel(Admin me) {
        this.me = me;
        setTitle("IRON TEMPLE - GLOBAL ADMINISTRATION");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(headerDark);

        accountsTab = new AdminAccountsPanel(this);
        membersTab = new AdminMembersPanel(this);
        coachesTab = new AdminCoachesPanel(this);
        billingTab = new AdminBillingPanel(this);

        tabs = new JTabbedPane(JTabbedPane.TOP);
        styleTabPane(tabs);

        tabs.addTab("SECURITY ACCOUNTS", accountsTab);
        tabs.addTab("MEMBERSHIP", membersTab);
        tabs.addTab("COACHING STAFF", coachesTab);
        tabs.addTab("FINANCIALS", billingTab);
        tabs.addTab("ANALYTICS & REPORTS", new AdminReportsPanel(this));

        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(headerDark);
        topNav.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblUser = new JLabel("<html><span style='color:#555555;'>OPERATOR:</span> <span style='color:#0096FF; font-weight:bold;'>" + me.getName().toUpperCase() + "</span></html>");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topNav.add(lblUser, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        actions.setOpaque(false);

        JButton btnLogout = new JButton("DISCONNECT SYSTEM");
        styleLogoutButton(btnLogout);
        btnLogout.addActionListener(e -> logout());

        actions.add(btnLogout);
        topNav.add(actions, BorderLayout.EAST);


        add(topNav, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void styleTabPane(JTabbedPane t) {
        t.setBackground(tabBg);
        t.setForeground(textDim);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setFocusable(false);
        t.setBorder(BorderFactory.createEmptyBorder());

        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("TabbedPane.borderHighlightColor", headerDark);
        UIManager.put("TabbedPane.darkShadow", headerDark);
        t.updateUI();
    }

    private void styleLogoutButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(new Color(255, 80, 80));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 80), 1));
        b.setPreferredSize(new Dimension(150, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }

    public void refreshAllTabs() {
        if (accountsTab != null) accountsTab.refresh();
        if (membersTab != null) membersTab.refresh();
        if (coachesTab != null) coachesTab.refresh();
        if (billingTab != null) billingTab.refresh();
    }
}