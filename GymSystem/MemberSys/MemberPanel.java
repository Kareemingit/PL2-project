package GymSystem.MemberSys;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    private Color sidebarColor = new Color(33, 37, 41); // Dark Sidebar
    private Color bgColor = new Color(240, 242, 245);    // Light Dashboard background
    private Color accentColor = new Color(52, 152, 219);  // Blue

    public MemberPanel(Account member) {
        this.member = member;
        setTitle("Member Portal - " + member.getName());
        setSize(1000, 700); // Wider for dashboard format
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblBrand = new JLabel("GYM PORTAL");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogout = new JButton("Sign Out");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleSidebarButton(btnLogout);
        btnLogout.addActionListener(e -> { this.dispose(); new LoginFrame().setVisible(true); });

        sidebar.add(lblBrand);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);


        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(bgColor);
        mainContent.setBorder(new EmptyBorder(30, 30, 30, 30));


        JLabel lblHeader = new JLabel("Member Dashboard");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        mainContent.add(lblHeader, BorderLayout.NORTH);


        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        String subEnd = fetchSubscriptionEndDate();
        String coachName = fetchCoachName();

        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.5; gbc.weighty = 0.15;
        grid.add(createStatCard("SUBSCRIPTION ENDS", subEnd, new Color(46, 204, 113)), gbc);

        gbc.gridx = 1;
        grid.add(createStatCard("PERSONAL COACH", coachName, accentColor), gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.weighty = 0.85;
        JTextArea txtPlan = createModuleTextArea("Your Training Plan");
        loadPlan(txtPlan);
        grid.add(new JScrollPane(txtPlan), gbc);

        gbc.gridx = 1;
        int coachId = fetchMemberCoachId();
        JTextArea txtMessages = createModuleTextArea("Coach Communications");
        loadMessages(txtMessages, coachId);
        grid.add(new JScrollPane(txtMessages), gbc);

        mainContent.add(grid, BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainContent, BorderLayout.CENTER);
        add(root);

        if (!subEnd.equals("N/A")) checkSubscriptionStatus(subEnd);
    }

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.SOUTH);
        return card;
    }

    private JTextArea createModuleTextArea(String title) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10,10,10,10));

        area.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5), title,
                0, 0, new Font("Segoe UI", Font.BOLD, 14), sidebarColor));
        return area;
    }

    private void styleSidebarButton(JButton b) {
        b.setPreferredSize(new Dimension(180, 40));
        b.setBackground(new Color(231, 76, 60)); // Logout Red
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private String fetchSubscriptionEndDate() {
        ArrayList<ArrayList<String>> membersData = Database.readMembers();
        for (ArrayList<String> m : membersData) {
            if (m.size() > 4 && Integer.parseInt(m.get(1).trim()) == member.getId()) return m.get(3).trim();
        }
        return "N/A";
    }

    private int fetchMemberCoachId() {
        ArrayList<ArrayList<String>> membersData = Database.readMembers();
        for (ArrayList<String> m : membersData) {
            if (m.size() > 4 && Integer.parseInt(m.get(1).trim()) == member.getId()) return Integer.parseInt(m.get(4).trim());
        }
        return 0;
    }

    private String fetchCoachName() {
        int coachId = fetchMemberCoachId();
        if (coachId > 0) {
            for (ArrayList<String> c : Database.readCoachs()) {
                if (Integer.parseInt(c.get(0).trim()) == coachId) return c.get(2).trim();
            }
        }
        return "No Coach";
    }

    private void checkSubscriptionStatus(String dateStr) {
        try {
            LocalDate endDate = LocalDate.parse(dateStr, GymManagmentSystem.DATE_FMT);
            LocalDate today = LocalDate.now();
            if (endDate.isBefore(today)) {
                JOptionPane.showMessageDialog(this, "Subscription Expired on " + dateStr, "Status", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {}
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
                            area.append("● [" + p[3] + "]: " + p[4].replace(";", ",") + "\n\n");
                        }
                    }
                }
            }
        } catch (Exception ex) {}
    }

    private void loadPlan(JTextArea area) {
        try {
            Path path = Paths.get("data", "schedules.csv");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] p = line.split(",");
                    if (p.length >= 6 && Integer.parseInt(p[2].trim()) == member.getId()) {
                        area.append("DETAILS: " + p[5].replace(";", ",") + "\n");
                        area.append("TIME: " + p[3] + " - " + p[4] + "\n\n");
                    }
                }
            }
        } catch (Exception ex) {}
    }
}