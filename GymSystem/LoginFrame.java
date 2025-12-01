package GymSystem;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.AdminSys.Admin;
import GymSystem.AdminSys.AdminPanel;

public class LoginFrame extends JFrame {
    JTextField txtUser;
    JPasswordField txtPass;
    public LoginFrame() {
        setTitle("Gym Management System - Login");
        setSize(360,220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(3,2,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Username:"));
        txtUser = new JTextField();
        p.add(txtUser);
        p.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        p.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> doLogin());
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        JPanel btnP = new JPanel();
        btnP.add(btnLogin);
        btnP.add(btnExit);

        add(p, BorderLayout.CENTER);
        add(btnP, BorderLayout.SOUTH);

        // enter to login
        txtPass.addActionListener(e->doLogin());
        txtUser.addActionListener(e->txtPass.requestFocusInWindow());
    }
    
    Account authenticate(String username , String password){
        ArrayList<Account> accounts = Database.readAccounts();
        for (Account account : accounts) {
            if(account.username.equals(username) && account.password.equals(password))
                return account;
        }
        return null;
    }

    void doLogin() {
        String un = txtUser.getText().trim();
        String pw = new String(txtPass.getPassword());
        if (un.isEmpty() || pw.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Enter username and password"); 
            return; 
        }
        Account a = authenticate(un, pw);
        if (a == null) { JOptionPane.showMessageDialog(this, "Invalid credentials"); return; }
        SRole accountRole = a.role;
        switch (accountRole) {
            case ADMIN:
                Admin admin = new Admin(a.id , a.username , a.password , a.name , a.email , a.phone);
                AdminPanel p = new AdminPanel(admin);
                p.setVisible(true);
                break;
            case COACH:
                //CoachPanel p = new CoachPanel();
            case MEMBER:
                //MemberPanel p = new MemberPanel();
            case USER:
                //UserPanel p = new UserPanel();
            default:
                break;
        }
    }

    void checkSubscriptionNotifications() {

    }
}