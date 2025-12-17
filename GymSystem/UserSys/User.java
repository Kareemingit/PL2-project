package GymSystem.UserSys;

import GymSystem.Account;

public class User extends Account{

    public User(int id, String username, String password,
                String name, String email, String phone) {
        super(id, username, password, SRole.USER, name, email, phone);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void updateInformation(String username, String password, String name, String email, String phone) {
        // 1. Update local object memory
        if (username != null && !username.isEmpty()) this.username = username;
        if (password != null && !password.isEmpty()) this.password = password;
        if (name != null && !name.isEmpty()) this.name = name;
        if (email != null && !email.isEmpty()) this.email = email;
        if (phone != null && !phone.isEmpty()) this.phone = phone;

        GymSystem.Database.updateAccount(
                this.id,
                this.username,
                this.password,
                this.role.name(),
                this.name,
                this.email,
                this.phone
        );
    }
}