package GymSystem.UserSys;

//import GymSystem.GUI.LoginFrame; // تعديل المسار ليقرأ من فولدر GUI
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

        setVisible(true); // مهمة عشان الشاشة تظهر
    }

    private void updateInfo() {
        String newUsername = JOptionPane.showInputDialog(this, "Enter new username:", currentUser.getUsername());
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:", currentUser.getPassword());
        String newName     = JOptionPane.showInputDialog(this, "Enter new name:", currentUser.getName());
        String newEmail    = JOptionPane.showInputDialog(this, "Enter new email:", currentUser.getEmail());
        String newPhone    = JOptionPane.showInputDialog(this, "Enter new phone:", currentUser.getPhone());

        if(newUsername != null && newPassword != null && newName != null && newEmail != null && newPhone != null) {
            currentUser.updateInformation(newUsername, newPassword, newName, newEmail, newPhone);
            JOptionPane.showMessageDialog(this, "Information updated successfully!");

            setTitle("User - " + newUsername);
            revalidate();
            repaint();
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