package GymSystem.AdminSys;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import GymSystem.*;
import GymSystem.Account.SRole;

class AdminAccountsPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;
    public AdminAccountsPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Username","Role","Name","Email","Phone"},0) {
            public boolean isCellEditable(int row,int col){return false;}
        };
        tbl = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton btnAdd = new JButton("Add Account"); btnAdd.addActionListener(e->addAccount());
        JButton btnEdit = new JButton("Edit Account"); btnEdit.addActionListener(e->editAccount());
        JButton btnDelete = new JButton("Delete Account"); btnDelete.addActionListener(e->deleteAccount());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e->searchAccount());
        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete); top.add(btnSearch);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    void refresh() {
        model.setRowCount(0);

        ArrayList<ArrayList<String>> accounts = Database.readAccounts();

        for (ArrayList<String> u : accounts) {
            model.addRow(new Object[]{
                u.get(0), // id
                u.get(4), // name
                u.get(3), // role
                u.get(1), // username
                u.get(5), // email
                u.get(6)  // phone
            });
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
    }
    void deleteAccount() {
    }
    void searchAccount() {
    }
}

class UserFormDialog extends JDialog {
    JTextField txtUser, txtName, txtEmail, txtPhone, txtExtra;
    JPasswordField txtPass;
    JComboBox<String> cbRole;
    boolean saved=false;
    String username, password, name, email, phone, extra="";
    SRole role;
    public UserFormDialog(Account existing) {
        setModal(true);
        setSize(420,320);
        setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(7,2,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Username:")); txtUser = new JTextField(); p.add(txtUser);
        p.add(new JLabel("Password:")); txtPass = new JPasswordField(); p.add(txtPass);
        p.add(new JLabel("Role:")); cbRole = new JComboBox<>(new String[]{"USER","MEMBER","COACH","ADMIN"}); p.add(cbRole);
        p.add(new JLabel("Full Name:")); txtName = new JTextField(); p.add(txtName);
        p.add(new JLabel("Email:")); txtEmail = new JTextField(); p.add(txtEmail);
        p.add(new JLabel("Phone:")); txtPhone = new JTextField(); p.add(txtPhone);
        p.add(new JLabel("Extra (coach specialty):")); txtExtra = new JTextField(""); p.add(txtExtra);

        JPanel btn = new JPanel();
        JButton ok = new JButton("Save"); ok.addActionListener(e->onSave());
        JButton cancel = new JButton("Cancel"); cancel.addActionListener(e->{ saved=false; dispose();});
        btn.add(ok); btn.add(cancel);

        add(p, BorderLayout.CENTER);
        add(btn, BorderLayout.SOUTH);
    }
    void onSave() {
        username = txtUser.getText().trim();
        password = new String(txtPass.getPassword());
        name = txtName.getText().trim();
        email = txtEmail.getText().trim();
        phone = txtPhone.getText().trim();
        extra = txtExtra.getText().trim();
        String r = (String)cbRole.getSelectedItem();
        role = SRole.valueOf(r);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required"); return;
        }
        saved = true;
        dispose();
    }
}

class AdminMembersPanel extends JPanel {
    AdminPanel parent;
    JTable tbl; DefaultTableModel model;
    public AdminMembersPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Account ID","Name","SubscriptionEnd","Coach"},0){ public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton btnAdd = new JButton("Add Member"); btnAdd.addActionListener(e->addMember());
        JButton btnEdit = new JButton("Edit Member"); btnEdit.addActionListener(e->editMember());
        JButton btnDelete = new JButton("Delete Member"); btnDelete.addActionListener(e->deleteMember());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e->searchMember());
        JButton btnAssign = new JButton("Assign Coach"); btnAssign.addActionListener(e->assignCoach());
        JButton btnRefresh = new JButton("Refresh"); btnRefresh.addActionListener(e->refresh());
        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete); top.add(btnSearch); top.add(btnAssign); top.add(btnRefresh);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> members = Database.readMembers();
        for (ArrayList<String> u : members) {
            model.addRow(new Object[]{
                u.get(0), // id
                u.get(1), // account id
                u.get(2), // name
                u.get(3), // SubscriptionEnd
                u.get(4) // coach id
            });
        }
    }

    void addMember() {
    }

    void editMember() {
    }

    void deleteMember() {
    }

    void searchMember() {
    }

    void assignCoach() {
    }
}

class AdminCoachesPanel extends JPanel {
    AdminPanel parent;
    JTable tbl; DefaultTableModel model;
    public AdminCoachesPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Account ID","Name","Specialty","Email"},0){ public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton btnAdd = new JButton("Add Coach"); btnAdd.addActionListener(e->addCoach());
        JButton btnEdit = new JButton("Edit Coach"); btnEdit.addActionListener(e->editCoach());
        JButton btnDelete = new JButton("Delete Coach"); btnDelete.addActionListener(e->deleteCoach());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e->searchCoach());
        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete); top.add(btnSearch);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }
    void refresh() {
        model.setRowCount(0);
        ArrayList<ArrayList<String>> coaches = Database.readCoachs();
        for (ArrayList<String> c: coaches) {
            model.addRow(new Object[]{
                c.get(0),
                c.get(1),
                c.get(2), 
                c.get(3),});
        }
    }
    
    void addCoach() {
        String name = JOptionPane.showInputDialog(this, "Full name:");
        if (name==null) name="";
        String specialty = JOptionPane.showInputDialog(this, "Specialty:");
        if (specialty==null) specialty="";
        parent.me.addCoach(-1, -1, name, specialty);        
        refresh();
    }
    void editCoach() {
    }
    void deleteCoach() {
    }
    void searchCoach() {
    }
}

class AdminBillingPanel extends JPanel {
    AdminPanel parent;
    JTable tbl; DefaultTableModel model;
    public AdminBillingPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Member","Amount","Date","Notes"},0){ public boolean isCellEditable(int r,int c){return false;} };
        tbl = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton btnAdd = new JButton("Add Billing"); btnAdd.addActionListener(e->addBilling());
        JButton btnDelete = new JButton("Delete"); btnDelete.addActionListener(e->deleteBilling());
        JButton btnRefresh = new JButton("Refresh"); btnRefresh.addActionListener(e->refresh());
        top.add(btnAdd); top.add(btnDelete); top.add(btnRefresh);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }
    void refresh() {
    }
    void addBilling() {
    }
    void deleteBilling() {
    }
}

class AdminReportsPanel extends JPanel {
    AdminPanel parent;
    public AdminReportsPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        JPanel c = new JPanel(new GridLayout(5,1,6,6));
        c.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
}

class AdminUsersPanel extends JPanel {
    AdminPanel parent;
    JTable tbl;
    DefaultTableModel model;
    public AdminUsersPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Username","Role","Name","Email","Phone"},0) {
            public boolean isCellEditable(int row,int col){return false;}
        };
        tbl = new JTable(model);
        refresh();

        JPanel top = new JPanel();
        JButton btnAdd = new JButton("Add User"); btnAdd.addActionListener(e->addUser());
        JButton btnEdit = new JButton("Edit User"); btnEdit.addActionListener(e->editUser());
        JButton btnDelete = new JButton("Delete User"); btnDelete.addActionListener(e->deleteUser());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e->searchUser());
        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete); top.add(btnSearch);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    void refresh() {
    }

    void addUser() {
        
    }
    void editUser() {
    }
    void deleteUser() {
    }
    void searchUser() {
    }
}

public class AdminPanel extends JFrame {
    JTabbedPane tabs;
    Admin me;
    public AdminPanel(Admin me) {
        this.me = me;
        setTitle("Admin Dashboard");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();
        tabs.add("All Accounts", new AdminAccountsPanel(this));
        tabs.add("Members", new AdminMembersPanel(this));
        tabs.add("Coaches", new AdminCoachesPanel(this));
        tabs.add("Billing", new AdminBillingPanel(this));
        tabs.add("Reports", new AdminReportsPanel(this));
        add(tabs, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Logged in as: ADMIN");
        top.add(lbl, BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e->logout());
        top.add(btnLogout, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }
    void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
