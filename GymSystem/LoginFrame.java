package GymSystem;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.AdminSys.Admin;
import GymSystem.AdminSys.AdminPanel;
import GymSystem.UserSys.*;

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
    
    ArrayList<String> authenticate(String username , String password){
        ArrayList<ArrayList<String>> accounts = Database.readAccounts();
        for (ArrayList<String> account : accounts) {
            if(account.get(1).equals(username) && account.get(2).equals(password))
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
        ArrayList<String> a = authenticate(un, pw);
        if (a == null) { JOptionPane.showMessageDialog(this, "Invalid credentials"); return; }
        SRole accountRole = SRole.valueOf(a.get(3));
        switch (accountRole) {
            case ADMIN:
                Admin admin = new Admin(Integer.parseInt(a.get(0)) , a.get(1) , a.get(2) , a.get(4) , 
                    a.get(5) , a.get(6));
                AdminPanel ap = new AdminPanel(admin);
                ap.setVisible(true);
                break;
            case COACH:
                //CoachPanel p = new CoachPanel();
            case MEMBER:
                //MemberPanel p = new MemberPanel();
            case USER:
                User user = new User(Integer.parseInt(a.get(0)) , a.get(1) , a.get(2) , a.get(4) ,
                    a.get(5) , a.get(6)
                );
                UserPanel up = new UserPanel(user);
                up.setVisible(true);
            default:
                break;
        }
    }

    void checkSubscriptionNotifications() {

    }
}