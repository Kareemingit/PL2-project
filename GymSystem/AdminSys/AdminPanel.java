package GymSystem.AdminSys;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import GymSystem.*;
import GymSystem.Account.SRole;
import java.io.*;
import java.nio.file.*;

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
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an account to edit");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        ArrayList<String> record = Database.readAccounts()
                .stream()
                .filter(a -> Integer.parseInt(a.get(0)) == id)
                .findFirst()
                .orElse(null);

        if (record == null) return;

        Account acc = new Account(
                Integer.parseInt(record.get(0)),
                record.get(1),
                record.get(2),
                SRole.valueOf(record.get(3)),
                record.get(4),
                record.get(5),
                record.get(6)
        );

        UserFormDialog d = new UserFormDialog(acc);
        d.setVisible(true);

        if (d.saved) {
            parent.me.editAccount(
                    acc.getId(),
                    d.username,
                    d.password,
                    d.role,
                    d.name,
                    d.email,
                    d.phone
            );
            refresh();
        }
    }
    
    void deleteAccount() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an account to delete");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        SRole role = SRole.valueOf(model.getValueAt(row, 2).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "This action will permanently delete the account.\nContinue?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            parent.me.deleteAccountCascade(id, role);
            refresh();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }
    
    void searchAccount() {
        String keyword = JOptionPane.showInputDialog(
            this,
            "Search by ID, username, name, role, email, or phone:"
        );

        if (keyword == null) return; // user canceled

        keyword = keyword.trim().toLowerCase();

        if (keyword.isEmpty()) {
            refresh();
            return;
        }

        model.setRowCount(0);

        ArrayList<ArrayList<String>> accounts = Database.readAccounts();

        for (ArrayList<String> u : accounts) {

            boolean match = false;

            for (String field : u) {
                if (field.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }

            if (match) {
                model.addRow(new Object[]{
                        u.get(0), // ID
                        u.get(4), // Name
                        u.get(3), // Role
                        u.get(1), // Username
                        u.get(5), // Email
                        u.get(6)  // Phone
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching accounts found");
        }
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
        if (existing != null) {
            txtUser.setText(existing.getUsername());
            txtPass.setText(existing.getPassword());
            txtName.setText(existing.getName());
            txtEmail.setText(existing.getEmail());
            txtPhone.setText(existing.getPhone());
            cbRole.setSelectedItem(existing.getRole().name());
        }
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
        JTextField txtAccountId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtCoachId = new JTextField("0");
        JPanel panel = new JPanel(new GridLayout(3, 2, 6, 6));
        panel.add(new JLabel("Account ID:"));
        panel.add(txtAccountId);
        panel.add(new JLabel("Member Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Coach ID (0 = none):"));
        panel.add(txtCoachId);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add Member",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        String accIdStr = txtAccountId.getText().trim();
        String name = txtName.getText().trim();
        String coachIdStr = txtCoachId.getText().trim();

        if (accIdStr.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account ID and Name are required");
            return;
        }
        int accountId, coachId;
        try {
            accountId = Integer.parseInt(accIdStr);
            coachId = coachIdStr.isEmpty() ? 0 : Integer.parseInt(coachIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid numeric values");
            return;
        }
        if (accountId > 0 && Database.readAccounts().stream()
                .noneMatch(a -> Integer.parseInt(a.get(0)) == accountId)) {
            JOptionPane.showMessageDialog(this, "Account ID does not exist");
            return;
        }
        parent.me.addMember(-1, accountId, name, coachId);
        refresh();
    }

    void editMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to edit");
            return;
        }
        int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());

        ArrayList<ArrayList<String>> members = Database.readMembers();
        ArrayList<String> record = null;

        for (ArrayList<String> m : members) {
            if (Integer.parseInt(m.get(0)) == memberId) {
                record = m;
                break;
            }
        }

        if (record == null) return;

        JTextField txtName = new JTextField(record.get(2));
        JTextField txtEndDate = new JTextField(record.get(3));
        JTextField txtCoachId = new JTextField(record.get(4));

        JPanel panel = new JPanel(new GridLayout(3, 2, 6, 6));
        panel.add(new JLabel("Member Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Subscription End (YYYY-MM-DD):"));
        panel.add(txtEndDate);
        panel.add(new JLabel("Coach ID (0 = none):"));
        panel.add(txtCoachId);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Member",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        String name = txtName.getText().trim();
        String endDate = txtEndDate.getText().trim();
        String coachStr = txtCoachId.getText().trim();

        if (name.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and subscription date are required");
            return;
        }

        int coachId;
        try {
            coachId = coachStr.isEmpty() ? 0 : Integer.parseInt(coachStr);
            LocalDate.parse(endDate);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or coach ID");
            return;
        }

        parent.me.editMember(
            memberId,
            Integer.parseInt(record.get(1)),
            name,
            endDate,
            coachId
        );
        refresh();
    }

    void deleteMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to delete");
            return;
        }

        int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int accountId = Integer.parseInt(model.getValueAt(row, 1).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "This will permanently delete the member.\n"
            + "If an account exists, it will also be deleted.\n\nContinue?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        parent.me.deleteMemberCascade(memberId, accountId);
        refresh();

    }

    void searchMember() {
        String keyword = JOptionPane.showInputDialog(
            this,
            "Search by ID, Account ID, Name, Date, or Coach ID:"
        );

        if (keyword == null) return;

        keyword = keyword.trim().toLowerCase();

        if (keyword.isEmpty()) {
            refresh();
            return;
        }

        model.setRowCount(0);

        ArrayList<ArrayList<String>> members = Database.readMembers();

        for (ArrayList<String> m : members) {

            boolean match = false;

            for (String field : m) {
                if (field.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }

            if (match) {
                model.addRow(new Object[]{
                        m.get(0), // Member ID
                        m.get(1), // Account ID
                        m.get(2), // Name
                        m.get(3), // Subscription End
                        m.get(4)  // Coach ID
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching members found");
        }
    }

    void assignCoach() {

        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member first");
            return;
        }

        int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int accountId = Integer.parseInt(model.getValueAt(row, 1).toString());
        String name = model.getValueAt(row, 2).toString();
        String endDate = model.getValueAt(row, 3).toString();

        ArrayList<ArrayList<String>> coaches = Database.readCoachs();
        if (coaches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No coaches available");
            return;
        }

        String[] coachOptions = coaches.stream()
                .map(c -> c.get(0) + " - " + c.get(2) + " (" + c.get(3) + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select a coach:",
            "Assign Coach",
            JOptionPane.PLAIN_MESSAGE,
            null,
            coachOptions,
            coachOptions[0]
        );

        if (selected == null) return;

        int coachId = Integer.parseInt(selected.split(" - ")[0]);

        parent.me.assignCoachToMember(
            memberId,
            accountId,
            name,
            endDate,
            coachId
        );

        refresh();
    }

}

class AdminCoachesPanel extends JPanel {
    AdminPanel parent;
    JTable tbl; DefaultTableModel model;
    public AdminCoachesPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Account ID","Name","Specialty"},0){ public boolean isCellEditable(int r,int c){return false;} };
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
            if (c.size()>=4){
            model.addRow(new Object[]{
                c.get(0),
                c.get(1),
                c.get(2), 
                c.get(3),});}
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
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach to edit");
            return;
        }

        int coachId = Integer.parseInt(model.getValueAt(row, 0).toString());

        ArrayList<ArrayList<String>> all = Database.readCoachs();
        ArrayList<String> record = null;

        for (ArrayList<String> c : all) {
            if (Integer.parseInt(c.get(0)) == coachId) {
                record = c;
                break;
            }
        }

        if (record == null) return;

        JTextField txtName = new JTextField(record.get(2));
        JTextField txtSpecialty = new JTextField(record.get(3));

        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        panel.add(new JLabel("Full Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Specialty:"));
        panel.add(txtSpecialty);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Coach",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        String name = txtName.getText().trim();
        String specialty = txtSpecialty.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty");
            return;
        }

        parent.me.editCoach(
                coachId,
                Integer.parseInt(record.get(1)),
                name,
                specialty
        );

        refresh();
    }
    
    void deleteCoach() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach to delete");
            return;
        }

        int coachId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deleting this coach will unassign all members.\n\nContinue?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        parent.me.deleteCoachCascade(coachId);
        refresh();

    }
    
    void searchCoach() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "Search by ID, Name, Specialty, or Phone:"
        );
        if (keyword == null) return;

        keyword = keyword.trim().toLowerCase();

        if (keyword.isEmpty()) {
            refresh();
            return;
        }

        model.setRowCount(0);

        ArrayList<ArrayList<String>> coaches = Database.readCoachs();

        for (ArrayList<String> c : coaches) {

            boolean match = false;

            for (String field : c) {
                if (field.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                model.addRow(new Object[]{
                        c.get(0), // Coach ID
                        c.get(1), // Account ID (if exists)
                        c.get(2), // Name
                        c.get(3)  // Specialty / Phone
                });
            }
        }
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching coaches found");
        }
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
        model.setRowCount(0);
        ArrayList<ArrayList<String>> billList = Database.readBillings();
        for (ArrayList<String> b : billList) {
            model.addRow(new Object[]{
                    b.get(0), // ID
                    b.get(1), // Member
                    b.get(2), // amount
                    b.get(3), // date
                    b.get(4) // note
                }
            );
        }
    }

    void addBilling() {
        JTextField txtMemberId = new JTextField();
        JTextField txtAmount = new JTextField();
        JTextField txtDate = new JTextField();
        JTextField txtNote = new JTextField();
        Object[] form = {
                "Member ID:", txtMemberId,
                "Amount:", txtAmount,
                "Date:", txtDate,
                "Note:", txtNote
        };

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Billing",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            int memberId = Integer.parseInt(txtMemberId.getText().trim());
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String date = txtDate.getText().trim();
            String note = txtNote.getText().trim();
            if(note.isEmpty())
                note = "-";
            parent.me.addBilling(-1 ,memberId, amount, date, note);
            refresh();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid numeric input");
        }
    }
    
    void deleteBilling() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a billing record to delete");
            return;
        }
        int billingId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "This billing record will be permanently deleted.\n\nContinue?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        parent.me.deleteBilling(billingId);
        refresh();
    }
}

class ReportFrame extends JFrame{
    AdminPanel parent;
    JTextArea textArea;
    public ReportFrame(String defaultText){
        this.setSize(500, 400);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        textArea.append(defaultText);
        JButton saveButton = new JButton("Save Report");
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(saveButton, BorderLayout.SOUTH);
        saveButton.addActionListener(e->saveReportToFile(textArea));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void saveReportToFile(JTextArea textArea){
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
    public AdminReportsPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        JPanel c = new JPanel(new GridLayout(5,1,6,6));
        c.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JButton btnMembersCSV = new JButton("Import Members Report (txt)");
        btnMembersCSV.addActionListener(e->importReportTXT());
        JButton btnExpiring = new JButton("Make Report");
        btnExpiring.addActionListener(e->makeReport());
        c.add(btnMembersCSV); c.add(btnExpiring);
        add(c, BorderLayout.NORTH);
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
                JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while reading the file:\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    void makeReport(){
        ReportFrame rf = new ReportFrame("");
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
