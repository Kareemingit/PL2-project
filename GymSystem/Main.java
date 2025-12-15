package GymSystem;

import GymSystem.UserSys.User;
import GymSystem.UserSys.UserPanel;
import GymSystem.GUI.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // 1. تجربة شاشة الدخول أولاً
        // new LoginFrame();

        // 2. أو تجربة شاشة المستخدم مباشرة (سنقوم بإنشاء مستخدم وهمي للتجربة)
        User testUser = new User(1, "AhmedAli", "123456", "Ahmed Ali", "ahmed@test.com", "0100000000");
        
        // فتح شاشة المستخدم
        new UserPanel(testUser);
        
        System.out.println("System started...");
    }
}