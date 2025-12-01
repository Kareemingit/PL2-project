package GymSystem.AdminSys;
import GymSystem.Account;
import GymSystem.Database;

public class Admin extends Account{
    public Admin() {
        super();
        this.role = SRole.ADMIN;
    }
    
    public Admin(int id, String username, String password,
                String name, String email, String phone) {

        super(id, username, password, SRole.ADMIN, name, email, phone);
    }

    public void addAccount(int id, String username, String password, SRole role,String name, 
                            String email, String phone , String specialty){
        Database.writeAccount(id , username , password , role , name , email , phone);
        if(role == SRole.COACH){
        }
        else if(role == SRole.MEMBER){
            
        }
        else if(role == SRole.USER){

        }
    }

    public void addCoach(){
    }

    public void addMember(){
    } 

}
