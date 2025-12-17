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
        model = new DefaultTableModel(new Object[]{"ID","Username","Role","Name","Email","Phone"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tbl = new JTable(model);
        refresh();

        JPanel topPanel = new JPanel();

        JButton btnAdd = new JButton("Add Account"); btnAdd.addActionListener(e -> addAccount());
        JButton btnEdit = new JButton("Edit Account"); btnEdit.addActionListener(e -> editAccount());
        JButton btnDelete = new JButton("Delete Account"); btnDelete.addActionListener(e -> deleteAccount());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e -> searchAccount());

        // Create and link the Refresh button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refresh());

        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    void refresh() {
        model.setRowCount(0);

        ArrayList<ArrayList<String>> accounts = Database.readAccounts();

        for (ArrayList<String> u : accounts) {
            if (u.size() >= 7) {
            model.addRow(new Object[]{
                u.get(0), // id
                u.get(4), // name
                u.get(3), // role
                u.get(1), // username
                u.get(5), // email
                u.get(6)  // phone
            });}
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
                "This action will permanently delete the account and all linked data.\nContinue?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            parent.me.deleteAccountCascade(id, role);

            parent.refreshAllTabs();

            JOptionPane.showMessageDialog(this, "Account and all linked records deleted successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    void searchAccount() {
        String keyword = JOptionPane.showInputDialog(
            this,
            "Search by ID, username, name, role, email, or phone:"
        );

        if (keyword == null) return;

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
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtCoachId = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Member Name:")); panel.add(txtName);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Coach ID (0=none):")); panel.add(txtCoachId);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int coachId = Integer.parseInt(txtCoachId.getText().trim());

            if (coachId != 0 && !Database.checkIfIdExistsInFile(coachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "❌ Error: Coach ID " + coachId + " does not exist!");
                return;
            }

            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            parent.me.addMemberExtended(-1, -1,
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    coachId);

            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Member and Account created successfully!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers only.");
        }
    }

    void editMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to edit");
            return;
        }
        int mid = Integer.parseInt(model.getValueAt(row, 0).toString());
        int aid = Integer.parseInt(model.getValueAt(row, 1).toString());
        String name = model.getValueAt(row, 2).toString();
        String date = model.getValueAt(row, 3).toString();
        String currentCoach = model.getValueAt(row, 4).toString();

        String input = JOptionPane.showInputDialog(this, "Enter new Coach ID:", currentCoach);
        if (input == null) return;

        try {
            int newCoachId = Integer.parseInt(input.trim());
            if (newCoachId != 0 && !Database.checkIfIdExistsInFile(newCoachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "❌ Error: Coach ID " + newCoachId + " does not exist in the system!");
                return;
            }
            Database.updateMember(mid, aid, name, date, newCoachId);
            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Member updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter a numeric ID.");
        }
    }

    void deleteMember() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member to delete");
            return;
        }
        int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int accId = Integer.parseInt(model.getValueAt(row, 1).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete Member and their Login Account?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            parent.me.deleteMemberAndAccount(memberId, accId);
            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Member and Account deleted successfully.");
        }
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
                        m.get(3), // Subsc End
                        m.get(4)  // Coach ID
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching members found");
        }
    }

    void assignCoach() {
        String input = JOptionPane.showInputDialog(this, "Enter Coach ID to assign:");
        if (input == null || input.isEmpty()) return;

        try {
            int coachId = Integer.parseInt(input.trim());

            if (!Database.checkIfIdExistsInFile(coachId, "coaches.csv")) {
                JOptionPane.showMessageDialog(this, "❌ Error: Coach ID " + coachId + " does not exist!");
                return;
            }

            int row = tbl.getSelectedRow();
            int memberId = Integer.parseInt(model.getValueAt(row, 0).toString());
            int accId = Integer.parseInt(model.getValueAt(row, 1).toString());
            String name = model.getValueAt(row, 2).toString();
            String date = model.getValueAt(row, 3).toString();

            Database.updateMember(memberId, accId, name, date, coachId);
            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Coach assigned successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        }
    }

}

class AdminCoachesPanel extends JPanel {
    AdminPanel parent;
    JTable tbl; DefaultTableModel model;

    public AdminCoachesPanel(AdminPanel p) {
        this.parent = p;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Account ID","Name","Specialty"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tbl = new JTable(model);
        refresh();
        JPanel btnP = new JPanel();

        JButton btnAdd = new JButton("Add Coach"); btnAdd.addActionListener(e->addCoach());
        JButton btnEdit = new JButton("Edit Coach"); btnEdit.addActionListener(e->editCoach());
        JButton btnDelete = new JButton("Delete Coach"); btnDelete.addActionListener(e->deleteCoach());
        JButton btnSearch = new JButton("Search"); btnSearch.addActionListener(e->searchCoach());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refresh());

        btnP.add(btnAdd);
        btnP.add(btnEdit);
        btnP.add(btnDelete);
        btnP.add(btnSearch);
        btnP.add(btnRefresh);
        add(btnP, BorderLayout.NORTH);
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
        JTextField txtName = new JTextField();
        JTextField txtSpecialty = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Full Name:")); panel.add(txtName);
        panel.add(new JLabel("Specialty:")); panel.add(txtSpecialty);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Coach", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String specialty = txtSpecialty.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || specialty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Specialty are required!");
                return;
            }

            parent.me.addCoachExtended(-1, -1, name, specialty, email, phone);
            refresh();
        }
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
        int accId = Integer.parseInt(model.getValueAt(row, 1).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete Coach and their Login Account?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            parent.me.deleteCoachAndAccount(coachId, accId);
            parent.refreshAllTabs();
            JOptionPane.showMessageDialog(this, "Coach and Login Account deleted.");
        }
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
    AdminAccountsPanel accountsTab;
    AdminMembersPanel membersTab;
    AdminCoachesPanel coachesTab;
    AdminBillingPanel billingTab;
    public AdminPanel(Admin me) {
        this.me = me;
        setTitle("Admin Dashboard");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        accountsTab = new AdminAccountsPanel(this);
        membersTab = new AdminMembersPanel(this);
        coachesTab = new AdminCoachesPanel(this);
        billingTab = new AdminBillingPanel(this);

        tabs = new JTabbedPane();
        tabs.add("All Accounts", accountsTab);
        tabs.add("Members", membersTab);
        tabs.add("Coaches", coachesTab);
        tabs.add("Billing", billingTab);
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
    public void refreshAllTabs() {
        if (accountsTab != null) accountsTab.refresh();
        if (membersTab != null) membersTab.refresh();
        if (coachesTab != null) coachesTab.refresh();
        if (billingTab != null) billingTab.refresh();
    }
}
