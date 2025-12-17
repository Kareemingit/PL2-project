package GymSystem.CoachSys;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import GymSystem.Database;

public class CoachPanel extends JFrame {
    private CoachManager coachManager;
    private JTabbedPane tabbedPane;
    private JComboBox<String> memberSelect;
    private JTextArea txtPlanDetails;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextArea txtMessageContent;

    public CoachPanel(Coach coach) {
        this.coachManager = new CoachManager(coach);
        setTitle("Coach Panel - " + coach.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Create Member Plan", createPlanPanel());
        tabbedPane.addTab("Send Message", createMessagePanel());
        add(tabbedPane);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            this.dispose();
            new GymSystem.LoginFrame().setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPlanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("Select Member:"));
        memberSelect = new JComboBox<>();
        loadMembers();
        formPanel.add(memberSelect);

        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField(LocalDate.now().toString());
        formPanel.add(txtStartDate);

        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        txtEndDate = new JTextField(LocalDate.now().plusMonths(1).toString());
        formPanel.add(txtEndDate);

        JPanel planDetailPanel = new JPanel(new BorderLayout());
        planDetailPanel.add(new JLabel("Plan Details:"), BorderLayout.NORTH);
        txtPlanDetails = new JTextArea(10, 40);
        txtPlanDetails.setLineWrap(true);
        planDetailPanel.add(new JScrollPane(txtPlanDetails), BorderLayout.CENTER);

        JButton btnCreatePlan = new JButton("Save Plan");
        btnCreatePlan.addActionListener(e -> doCreatePlan());

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(planDetailPanel, BorderLayout.CENTER);
        panel.add(btnCreatePlan, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        txtMessageContent = new JTextArea(10, 40);
        JButton btnSendMessage = new JButton("Send to All Members");
        btnSendMessage.addActionListener(e -> {
            if (coachManager.sendMessageToAllMembers(txtMessageContent.getText())) {
                JOptionPane.showMessageDialog(this, "Message Sent");
                txtMessageContent.setText("");
            }
        });
        panel.add(new JScrollPane(txtMessageContent), BorderLayout.CENTER);
        panel.add(btnSendMessage, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMembers() {
        ArrayList<ArrayList<String>> members = Database.readMembers();
        for (ArrayList<String> m : members) {
            memberSelect.addItem(m.get(2) + " (ID: " + m.get(1) + ")");
        }
    }

    private void doCreatePlan() {
        String item = (String) memberSelect.getSelectedItem();
        int mid = Integer.parseInt(item.substring(item.indexOf("ID: ") + 4, item.length() - 1));
        if (coachManager.createPlanForMember(mid, LocalDate.parse(txtStartDate.getText()),
                LocalDate.parse(txtEndDate.getText()), txtPlanDetails.getText())) {
            JOptionPane.showMessageDialog(this, "Plan Saved");
        }
    }
}