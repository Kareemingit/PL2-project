package GymSystem;

public class Account extends SystemEntity{
    public static enum SRole { ADMIN, COACH, MEMBER, USER }
    protected int id;
    protected String username;
    protected String password;
    protected SRole role;
    protected String name;
    protected String email;
    protected String phone;
    public Account() {
        this.id = 0;
        this.username = "";
        this.password = "";
        this.role = null;
        this.name = "";
        this.email = "";
       	this.phone = "";
    }
    public Account(int id, String username, String password, SRole role,
               String name, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    public int getId() {
        return id;
    }

    public void setId(int newId){
        if(newId > 0)
            id = newId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public SRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
