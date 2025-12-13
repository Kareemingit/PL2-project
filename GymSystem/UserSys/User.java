package GymSystem.src;

import GymSystem.UserSys.User;
import javax.swing.*;
import java.awt.*;

class UserPanel extends JFrame {
    private User currentUser; // current logged-in user

    public UserPanel(User user) {
        this.currentUser = user;

        setTitle("User - " + currentUser.getUsername());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display welcome message
        add(new JLabel("Welcome, " + currentUser.getName(), SwingConstants.CENTER), BorderLayout.CENTER);

        // Top panel with username and logout button
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Logged in as: " + currentUser.getUsername()), BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        top.add(btnLogout, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Button to update user information
        JButton btnEdit = new JButton("Update info");
        btnEdit.addActionListener(e -> updateInfo());
        add(btnEdit, BorderLayout.SOUTH);
    }

    void updateInfo() {
        // Open dialog to input new user information
        String newUsername = JOptionPane.showInputDialog(this, "Enter new username:", currentUser.getUsername());
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:", currentUser.getPassword());
        String newName     = JOptionPane.showInputDialog(this, "Enter new name:", currentUser.getName());
        String newEmail    = JOptionPane.showInputDialog(this, "Enter new email:", currentUser.getEmail());
        String newPhone    = JOptionPane.showInputDialog(this, "Enter new phone:", currentUser.getPhone());

        // Update user information if none of the fields are null
        if(newUsername != null && newPassword != null && newName != null && newEmail != null && newPhone != null) {
            currentUser.updateInformation(newUsername, newPassword, newName, newEmail, newPhone);
            JOptionPane.showMessageDialog(this, "Information updated successfully!");
        }
    }

    void logout() {
        // Close this window and return to login screen
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}