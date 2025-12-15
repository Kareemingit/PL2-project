package GymSystem.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    // تعريف العناصر
    JTextField userText;
    JPasswordField passText;
    JButton loginButton;

    public LoginFrame() {
        // إعدادات النافذة
        setTitle("Gym System - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // توسيط الشاشة
        setLayout(null); // استخدام تخطيط حر (Coordinates)

        // 1. نص "Username"
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        // 2. حقل إدخال الاسم
        userText = new JTextField(20);
        userText.setBounds(110, 30, 180, 25);
        add(userText);

        // 3. نص "Password"
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        // 4. حقل إدخال كلمة المرور
        passText = new JPasswordField(20);
        passText.setBounds(110, 70, 180, 25);
        add(passText);

        // 5. زر الدخول
        loginButton = new JButton("Login");
        loginButton.setBounds(110, 120, 100, 30);
        add(loginButton);

        // برمجة زر الدخول (اختياري)
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userText.getText();
                String pass = new String(passText.getPassword());
                
                // هنا تضع منطق التحقق من الدخول
                System.out.println("Attempting login for: " + user);
                // مثال: لو صح افتح الصفحة الرئيسية
                // new GymSystem.MainDashboard(); 
                // dispose();
            }
        });

        setVisible(true); // إظهار النافذة
    }

    // دالة Main لتجربة الشاشة لوحدها
    public static void main(String[] args) {
        new LoginFrame();
    }
}