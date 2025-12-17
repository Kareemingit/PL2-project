package GymSystem.MemberSys;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import GymSystem.Account;
import GymSystem.Database;
import GymSystem.LoginFrame;

public class MemberPanel extends JFrame {
    private Account member;

    public MemberPanel(Account member) {
        this.member = member;
        setTitle("Member Dashboard - " + member.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblWelcome = new JLabel("Welcome, " + member.getName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblWelcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1));
        String subEnd = "No subscription found";

        ArrayList<ArrayList<String>> membersData = Database.readMembers();
        for (ArrayList<String> m : membersData) {
            if (m.size() > 1 && Integer.parseInt(m.get(1)) == member.getId()) {
                subEnd = m.get(3); // Date is at index 3
                break;
            }
        }

        center.add(new JLabel("Subscription Ends: " + subEnd, SwingConstants.CENTER));
        add(center, BorderLayout.CENTER);

        // Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        add(btnLogout, BorderLayout.SOUTH);
    }
}