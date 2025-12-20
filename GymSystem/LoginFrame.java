package GymSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.AdminSys.Admin;
import GymSystem.AdminSys.AdminPanel;
import GymSystem.MemberSys.MemberPanel;
import GymSystem.UserSys.UserPanel;
import GymSystem.CoachSys.CoachPanel;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    private Color darkBg = new Color(18, 22, 33);
    private Color sidebarBlue = new Color(52, 152, 219);
    private Color inputBg = new Color(42, 46, 60);
    private Color textColor = new Color(240, 240, 245);

    public LoginFrame() {
        setTitle("Gym Management System - Secure Access");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(darkBg);

        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(sidebarBlue);

        JLabel lblLogoIcon = new JLabel("<html><div style='text-align: center;'>🏋️<br><br><span style='font-size: 24px;'>FCAIH GYM</span><br><span style='font-size: 12px; font-weight: normal; color: #D1EAFF;'>GYM MANAGEMENT SYSTEM  </span></div></html>");
        lblLogoIcon.setForeground(Color.WHITE);
        lblLogoIcon.setFont(new Font("Segoe UI", Font.BOLD, 48));
        brandPanel.add(lblLogoIcon);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel lblLoginTitle = new JLabel("Welcome Back");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLoginTitle.setForeground(Color.WHITE);
        gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblLoginTitle, gbc);

        JLabel lblSub = new JLabel("Sign in to access your dashboard.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(150, 150, 170));
        gbc.gridy = 1;
        formPanel.add(lblSub, gbc);

        gbc.gridwidth = 2; gbc.insets = new Insets(25, 50, 5, 50);
        gbc.gridy = 2;
        formPanel.add(createLabel("USERNAME"), gbc);

        txtUser = createStyledTextField();
        gbc.gridy = 3;
        formPanel.add(txtUser, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(15, 50, 5, 50);
        formPanel.add(createLabel("PASSWORD"), gbc);

        txtPass = createStyledPasswordField();
        gbc.gridy = 5;
        formPanel.add(txtPass, gbc);

        JButton btnLogin = new JButton("LOGIN");
        stylePrimaryButton(btnLogin);
        gbc.gridy = 6; gbc.insets = new Insets(30, 50, 10, 50);
        formPanel.add(btnLogin, gbc);

        JButton btnExit = new JButton("Exit Application");
        styleGhostButton(btnExit);
        gbc.gridy = 7; gbc.insets = new Insets(0, 50, 10, 50);
        formPanel.add(btnExit, gbc);

        mainPanel.add(brandPanel);
        mainPanel.add(formPanel);
        add(mainPanel);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
        txtPass.addActionListener(e -> doLogin());
        txtUser.addActionListener(e -> txtPass.requestFocusInWindow());
    }
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(180, 180, 200));
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 40));
        f.setBackground(inputBg);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 65, 85), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setPreferredSize(new Dimension(0, 40));
        f.setBackground(inputBg);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 65, 85), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return f;
    }

    private void stylePrimaryButton(JButton b) {
        b.setPreferredSize(new Dimension(0, 45));
        b.setBackground(sidebarBlue);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleGhostButton(JButton b) {
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setForeground(new Color(150, 150, 170));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }


    private ArrayList<String> authenticate(String username, String password) {
        ArrayList<ArrayList<String>> accounts = Database.readAccounts();
        for (ArrayList<String> account : accounts) {
            if (account.get(1).equals(username) && account.get(2).equals(password))
                return account;
        }
        return null;
    }

    private void doLogin() {
        String un = txtUser.getText().trim();
        String pw = new String(txtPass.getPassword()).trim();

        if (un.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all credentials.");
            return;
        }

        ArrayList<String> a = authenticate(un, pw);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
            return;
        }

        try {
            SRole accountRole = SRole.valueOf(a.get(3).trim().toUpperCase());

            switch (accountRole) {
                case ADMIN:
                    Admin admin = new Admin(Integer.parseInt(a.get(0)), a.get(1), a.get(2), a.get(3), a.get(4), a.get(5));
                    new AdminPanel(admin).setVisible(true);
                    this.dispose();
                    break;

                case MEMBER:
                    Account member = new Account(Integer.parseInt(a.get(0)), a.get(1), a.get(2), SRole.MEMBER, a.get(4), a.get(5), a.get(6));
                    new MemberPanel(member).setVisible(true);
                    this.dispose();
                    break;

                case USER:
                    GymSystem.UserSys.User user = new GymSystem.UserSys.User(Integer.parseInt(a.get(0)), a.get(1), a.get(2), a.get(4), a.get(5), a.get(6));
                    new UserPanel(user).setVisible(true);
                    this.dispose();
                    break;

                case COACH:
                    ArrayList<ArrayList<String>> coaches = Database.readCoachs();
                    ArrayList<String> coachData = coaches.stream()
                            .filter(c -> c.size() > 1 && c.get(1).equals(a.get(0)))
                            .findFirst().orElse(null);

                    if (coachData != null) {
                        GymSystem.CoachSys.Coach coach = new GymSystem.CoachSys.Coach(
                                Integer.parseInt(a.get(0)), a.get(1), a.get(2), a.get(4), a.get(5), a.get(6),
                                Integer.parseInt(coachData.get(0)), coachData.get(3));
                        new CoachPanel(coach).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Coach details missing from database.");
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "System Navigation Error: " + ex.getMessage());
        }
    }
}