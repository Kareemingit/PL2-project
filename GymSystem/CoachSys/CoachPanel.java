package GymSystem.CoachSys;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import GymSystem.Database;
import GymSystem.LoginFrame;
import javax.swing.border.EmptyBorder;


public class CoachPanel extends JFrame {
    private CoachManager coachManager;
    private JComboBox<String> memberSelect;
    private JTextArea txtPlanDetails;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextArea txtMessageContent;

    // Panel switching
    private CardLayout cardLayout = new CardLayout();
    private JPanel centerPanel = new JPanel(cardLayout);

    public CoachPanel(Coach coach) {
        this.coachManager = new CoachManager(coach);
        setTitle("Coach Panel - " + coach.getName());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR (Modern Structural Change) ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JLabel lblTitle = new JLabel("COACH MENU");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebar.add(lblTitle);

        JButton btnShowPlan = createSidebarButton("Create Plan");
        JButton btnShowMsg = createSidebarButton("Send Message");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(btnShowPlan);
        sidebar.add(btnShowMsg);
        sidebar.add(btnLogout);

        // --- CONTENT MODULES ---
        centerPanel.add(createPlanPanel(), "PLAN");
        centerPanel.add(createMessagePanel(), "MSG");

        // --- LISTENERS (Linking to your existing methods) ---
        btnShowPlan.addActionListener(e -> cardLayout.show(centerPanel, "PLAN"));
        btnShowMsg.addActionListener(e -> cardLayout.show(centerPanel, "MSG"));
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createPlanPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        // Form Card
        JPanel card = new JPanel(new GridLayout(6, 2, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        card.add(new JLabel("Select Member:"));
        memberSelect = new JComboBox<>();
        loadMembers(); // Calls your existing method
        card.add(memberSelect);

        card.add(new JLabel("Start Date:"));
        txtStartDate = new JTextField(LocalDate.now().toString());
        card.add(txtStartDate);

        card.add(new JLabel("End Date:"));
        txtEndDate = new JTextField(LocalDate.now().plusMonths(1).toString());
        card.add(txtEndDate);

        // Plan Details
        JPanel planDetailPanel = new JPanel(new BorderLayout(5, 5));
        planDetailPanel.setOpaque(false);
        planDetailPanel.add(new JLabel("Plan Details:"), BorderLayout.NORTH);
        txtPlanDetails = new JTextArea(10, 40);
        txtPlanDetails.setLineWrap(true);
        planDetailPanel.add(new JScrollPane(txtPlanDetails), BorderLayout.CENTER);

        JButton btnCreatePlan = new JButton("Save Training Plan");
        btnCreatePlan.setBackground(new Color(52, 152, 219));
        btnCreatePlan.setForeground(Color.WHITE);
        btnCreatePlan.setFocusPainted(false);
        btnCreatePlan.addActionListener(e -> doCreatePlan()); // Calls your existing method

        panel.add(card, BorderLayout.NORTH);
        panel.add(planDetailPanel, BorderLayout.CENTER);
        panel.add(btnCreatePlan, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Compose Message to All Members");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        txtMessageContent = new JTextArea(10, 40);
        txtMessageContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(txtMessageContent), BorderLayout.CENTER);

        JButton btnSendMessage = new JButton("Send Message to all traniees");
        btnSendMessage.setBackground(new Color(46, 204, 113));
        btnSendMessage.setForeground(Color.WHITE);
        btnSendMessage.setFocusPainted(false);
        btnSendMessage.addActionListener(e -> {
            if (coachManager.sendMessageToAllMembers(txtMessageContent.getText())) {
                JOptionPane.showMessageDialog(this, "Message Sent");
                txtMessageContent.setText("");
            }
        });
        panel.add(btnSendMessage, BorderLayout.SOUTH);
        return panel;
    }

    // --- YOUR ORIGINAL LOGIC (Unchanged) ---
    private void loadMembers() {
        ArrayList<ArrayList<String>> members = Database.readMembers();
        for (ArrayList<String> m : members) {
            memberSelect.addItem(m.get(2) + " (ID: " + m.get(1) + ")");
        }
    }

    private void doCreatePlan() {
        try {
            String item = (String) memberSelect.getSelectedItem();
            int mid = Integer.parseInt(item.substring(item.indexOf("ID: ") + 4, item.length() - 1));
            if (coachManager.createPlanForMember(mid, LocalDate.parse(txtStartDate.getText()),
                    LocalDate.parse(txtEndDate.getText()), txtPlanDetails.getText())) {
                JOptionPane.showMessageDialog(this, "Plan Saved");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: Check date format (YYYY-MM-DD)");
        }
    }

    private JButton createSidebarButton(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(180, 35));
        b.setFocusPainted(false);
        return b;
    }
}