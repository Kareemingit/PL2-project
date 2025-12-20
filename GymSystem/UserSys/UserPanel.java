package GymSystem.UserSys;

import GymSystem.LoginFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserPanel extends JFrame {
    private User currentUser;

    private Color sidebarColor = new Color(33, 37, 41);
    private Color bgColor = new Color(240, 242, 245);
    private Color accentColor = new Color(52, 152, 219);

    public UserPanel(User user) {
        this.currentUser = user;

        setTitle("Account Settings - " + currentUser.getUsername());
        setSize(850, 600); // Larger, more professional scale
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblLogo = new JLabel("USER PORTAL");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnProfile = createSidebarLink("My Profile", true);
        JButton btnLogout = createSidebarLink("Logout", false);

        btnLogout.addActionListener(e -> logout());

        sidebar.add(lblLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));
        sidebar.add(btnProfile);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setBackground(bgColor);
        contentArea.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel settingsCard = new JPanel();
        settingsCard.setLayout(new BoxLayout(settingsCard, BoxLayout.Y_AXIS));
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel lblTitle = new JLabel("Account Information");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsCard.add(lblTitle);
        settingsCard.add(Box.createRigidArea(new Dimension(0, 25)));

        JTextField unField = createStyledField(currentUser.getUsername());
        JTextField pwField = createStyledField(currentUser.getPassword());
        JTextField nameField = createStyledField(currentUser.getName());
        JTextField emailField = createStyledField(currentUser.getEmail());
        JTextField phoneField = createStyledField(currentUser.getPhone());

        addFormField(settingsCard, "Username", unField);
        addFormField(settingsCard, "Password", pwField);
        addFormField(settingsCard, "Full Name", nameField);
        addFormField(settingsCard, "Email Address", emailField);
        addFormField(settingsCard, "Phone Number", phoneField);

        JButton btnSave = new JButton("Update Profile Information");
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBackground(accentColor);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave.addActionListener(e -> {
            currentUser.updateInformation(
                    unField.getText().trim(),
                    pwField.getText().trim(),
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
        });

        settingsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsCard.add(btnSave);

        contentArea.add(settingsCard);

        root.add(sidebar, BorderLayout.WEST);
        root.add(contentArea, BorderLayout.CENTER);
        add(root);

        setVisible(true);
    }

    private JButton createSidebarLink(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(active ? Color.WHITE : new Color(180, 180, 180));
        btn.setBackground(sidebarColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void addFormField(JPanel panel, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private JTextField createStyledField(String text) {
        JTextField f = new JTextField(text);
        f.setMaximumSize(new Dimension(400, 35));
        f.setPreferredSize(new Dimension(400, 35));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return f;
    }

    private void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "LoginFrame not found!");
            }
        });
    }
}