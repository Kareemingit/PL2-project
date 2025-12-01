package GymSystem.AdminSys;
import GymSystem.Account;
import GymSystem.Database;
import GymSystem.CoachSys.*;
public class Admin extends Account{
    public Admin() {
        super();
        this.role = SRole.ADMIN;
    }
    
    public Admin(int id, String username, String password,
                String name, String email, String phone) {

        super(id, username, password, SRole.ADMIN, name, email, phone);
    }

    public void addAccount(Account account , String extra){
        Database.writeAccount(account);
        if(account.getRole() == SRole.COACH){
            Coach newCoach = (Coach)account;
            newCoach.setSpecialty(extra);
            Database.writeCoach(newCoach);
        }
        else if(account.getRole() == SRole.MEMBER){

        }
        else if(account.getRole() == SRole.USER){

        }
    }

    public void addCoach(){
        
    }

}
