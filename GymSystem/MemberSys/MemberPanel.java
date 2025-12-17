package GymSystem.MemberSys;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import GymSystem.Account;
import GymSystem.Database;
import GymSystem.GymManagmentSystem;
import GymSystem.LoginFrame;
import java.util.List;

public class MemberPanel extends JFrame {
    private Account member;
    private void checkSubscriptionStatus(String dateStr) {
        try {
            LocalDate endDate = LocalDate.parse(dateStr, GymManagmentSystem.DATE_FMT); //
            LocalDate today = LocalDate.now();

            if (endDate.isBefore(today)) {
                JOptionPane.showMessageDialog(this,
                        "NOTIFICATION: Your subscription has expired on " + dateStr + "!",
                        "Subscription Expired", JOptionPane.WARNING_MESSAGE);
            } else if (endDate.isBefore(today.plusDays(3))) {
                JOptionPane.showMessageDialog(this,
                        "NOTIFICATION: Your subscription expires in less than 3 days!",
                        "Expiry Warning", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Invalid date format in database: " + dateStr);
        }
    }
    public MemberPanel(Account member) {
        this.member = member;
        setTitle("Member Dashboard - " + member.getName());
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblWelcome = new JLabel("Welcome, " + member.getName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblWelcome, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        String subEnd = "N/A";
        String coachName = "None Assigned";
        int coachId = 0;

        ArrayList<ArrayList<String>> membersData = Database.readMembers();
        for (ArrayList<String> m : membersData) {
            if (m.size() > 4 && Integer.parseInt(m.get(1).trim()) == member.getId()) {
                subEnd = m.get(3).trim();
                coachId = Integer.parseInt(m.get(4).trim());
                break;
            }
        }

        if (coachId > 0) {
            ArrayList<ArrayList<String>> coaches = Database.readCoachs();
            for (ArrayList<String> c : coaches) {
                if (Integer.parseInt(c.get(0).trim()) == coachId) {
                    coachName = c.get(2).trim(); //
                    break;
                }
            }
        }

        infoPanel.add(new JLabel("Subscription Ends: " + subEnd, SwingConstants.CENTER)); //
        infoPanel.add(new JLabel("Your Coach: " + coachName, SwingConstants.CENTER)); //
        center.add(infoPanel);

        JTextArea txtPlan = new JTextArea(8, 20);
        txtPlan.setEditable(false);
        txtPlan.setText("--- Your Training Plan ---\n");
        loadPlan(txtPlan);
        center.add(new JLabel("Training Schedule:"));
        center.add(new JScrollPane(txtPlan));

        JTextArea txtMessages = new JTextArea(8, 20);
        txtMessages.setEditable(false);
        txtMessages.setText("--- Messages from Coach ---\n");
        loadMessages(txtMessages, coachId);
        center.add(new JLabel("Coach Notifications:"));
        center.add(new JScrollPane(txtMessages));

        add(new JScrollPane(center), BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        add(btnLogout, BorderLayout.SOUTH);

        if (!subEnd.equals("N/A")) {
            checkSubscriptionStatus(subEnd);
        }
    }

    private void loadMessages(JTextArea area, int myCoachId) {
        try {
            Path path = Paths.get("data", "messages.csv");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] p = line.split(",");
                    if (p.length >= 5) {
                        int senderId = Integer.parseInt(p[1].trim());
                        int receiverId = Integer.parseInt(p[2].trim());

                        if (senderId == myCoachId || receiverId == member.getId() || receiverId == 0) {
                            area.append("[" + p[3] + "]: " + p[4].replace(";", ",") + "\n");
                        }
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadPlan(JTextArea area) {
        try {
            Path path = Paths.get("data", "schedules.csv");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] p = line.split(",");
                    if (p.length >= 6 && Integer.parseInt(p[2].trim()) == member.getId()) {
                        area.append("Details: " + p[5].replace(";", ",") + "\n");
                        area.append("Timeline: " + p[3] + " to " + p[4] + "\n\n");
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}