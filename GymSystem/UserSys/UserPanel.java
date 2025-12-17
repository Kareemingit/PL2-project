package GymSystem.UserSys;

//import GymSystem.GUI.LoginFrame;
import GymSystem.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class UserPanel extends JFrame {
    private User currentUser;

    public UserPanel(User user) {
        this.currentUser = user;

        setTitle("User - " + currentUser.getUsername());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new JLabel("Welcome, " + currentUser.getName(), SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("  Logged in as: " + currentUser.getUsername()), BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        top.add(btnLogout, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JButton btnEdit = new JButton("Update info");
        btnEdit.addActionListener(e -> updateInfo());
        add(btnEdit, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateInfo() {
        JTextField unField = new JTextField(currentUser.getUsername());
        JTextField pwField = new JTextField(currentUser.getPassword());
        JTextField nameField = new JTextField(currentUser.getName());
        JTextField emailField = new JTextField(currentUser.getEmail());
        JTextField phoneField = new JTextField(currentUser.getPhone());

        Object[] message = {
                "Username:", unField,
                "Password:", pwField,
                "Full Name:", nameField,
                "Email:", emailField,
                "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            currentUser.updateInformation(
                    unField.getText().trim(),
                    pwField.getText().trim(),
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Profile Updated!");
        }
    }
    private void logout() {
        this.dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "LoginFrame not found! \nError: " + ex.getMessage());
            }
        });
    }
}