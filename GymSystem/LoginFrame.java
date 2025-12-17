package GymSystem;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.AdminSys.Admin;
import GymSystem.AdminSys.AdminPanel;
import GymSystem.MemberSys.MemberPanel;
import GymSystem.UserSys.User;
import GymSystem.UserSys.UserPanel;
import GymSystem.CoachSys.CoachPanel;

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
    //helper fun for dologin
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
                String adminName = (a.size() > 4) ? a.get(4) : "Admin";
                String adminEmail = (a.size() > 5) ? a.get(5) : "";
                Admin admin = new Admin(Integer.parseInt(a.get(0)) , a.get(1) , a.get(2) , a.get(3) , 
                    a.get(4) , a.get(5));
                AdminPanel p = new AdminPanel(admin);
                p.setVisible(true);
                break;

            case COACH:
                ArrayList<ArrayList<String>> coaches = Database.readCoachs();
                ArrayList<String> coachData = coaches.stream()
                        .filter(c -> c.size() > 1 && c.get(1).equals(a.get(0)))
                        .findFirst().orElse(null);

                if (coachData != null) {
                    int coachId = Integer.parseInt(coachData.get(0));
                    String specialty = coachData.get(3);
                    String name  = (a.size() > 4) ? a.get(4) : "No Name";
                    String email = (a.size() > 5) ? a.get(5) : "No Email";
                    String phone = (a.size() > 6) ? a.get(6) : "No Phone";
                    GymSystem.CoachSys.Coach coach = new GymSystem.CoachSys.Coach(
                            Integer.parseInt(a.get(0)), a.get(1), a.get(2), a.get(4), a.get(5), a.get(6),
                            coachId, specialty
                    );

                    GymSystem.CoachSys.CoachPanel cp = new GymSystem.CoachSys.CoachPanel(coach);
                    cp.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Coach data not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case MEMBER:
                Account mAcc = new Account(
                        Integer.parseInt(a.get(0)), a.get(1), a.get(2), SRole.MEMBER,
                        (a.size() > 4 ? a.get(4) : "Member"),
                        (a.size() > 5 ? a.get(5) : ""),
                        (a.size() > 6 ? a.get(6) : "")
                );
                new MemberPanel(mAcc).setVisible(true);
                this.dispose();
                break;
            case USER:
                GymSystem.UserSys.User userObj = new GymSystem.UserSys.User(
                        Integer.parseInt(a.get(0)), // ID
                        a.get(1),                   // Username
                        a.get(2),                   // Password
                        (a.size() > 4 ? a.get(4) : "User"), // Name
                        (a.size() > 5 ? a.get(5) : ""),     // Email
                        (a.size() > 6 ? a.get(6) : "")      // Phone
                );

                new GymSystem.UserSys.UserPanel(userObj).setVisible(true);
                this.dispose();
                break;

                default:
                break;
        }
    }

    void checkSubscriptionNotifications() {

    }
}