package GymSystem.CoachSys;
import GymSystem.Account;
import GymSystem.SystemEntity;
import GymSystem.Database;
import GymSystem.SystemEntity;
import java.time.LocalDate;

//====================================================
//==============تم الانتهاء============================
//====================================================

public class Coach extends Account{
    private int CoachId;
    private String specialty;
    public Coach(int id, String username, String password,
               String name, String email, String phone , int Cid , String specialty) { 
        super(id , username , password , SRole.COACH , name , email , phone);
        this.CoachId = Cid;
        this.specialty = specialty;
    }
    public int getCoachId() {
        return CoachId;
    }
    public void setCoachId(int Cid){
        if(Cid > 0)
            this.CoachId = Cid;
    }

    public String getSpecialty() {
        return specialty;
    }
    public void setSpecialty(String s){
        this.specialty = s;
    }
}
