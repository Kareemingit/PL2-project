package GymSystem.AdminSys;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        if (id < 0) {
            ArrayList<ArrayList<String>> all = Database.readAccounts();
            int maxId = -1;

            for (ArrayList<String> row : all) {
                if (row.isEmpty()) continue;

                try {
                    int rowId = Integer.parseInt(row.get(0));
                    if (rowId > maxId) maxId = rowId;
                } catch (NumberFormatException ignored) {}
            }

            id = maxId + 1;
        }
        Database.writeAccount(id , username , password , role , name , email , phone);
        if(role == SRole.COACH){
            addCoach(-1, id, name, specialty);
        }
        else if(role == SRole.MEMBER){
            addMember(-1, id , name , 0);
        }
    }

    public void addCoach(int CoachId , int accountId , String name , String specialty){
        if(CoachId < 0){
            ArrayList<ArrayList<String>> all = Database.readCoachs();
            int maxId = -1;

            for (ArrayList<String> row : all) {
                if (row.isEmpty()) continue;

                try {
                    int rowId = Integer.parseInt(row.get(0));
                    if (rowId > maxId) maxId = rowId;
                } catch (NumberFormatException ignored) {}
            }
            CoachId = maxId + 1;
        }
        Database.writeCoach(CoachId, accountId, name, specialty);
    }

    public void addMember(int MemberId , int accountId , String Mname , int CoachId){
        DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
        if(MemberId < 0){
            ArrayList<ArrayList<String>> all = Database.readMembers();
            int maxId = -1;

            for (ArrayList<String> row : all) {
                if (row.isEmpty()) continue;

                try {
                    int rowId = Integer.parseInt(row.get(0));
                    if (rowId > maxId) maxId = rowId;
                } catch (NumberFormatException ignored) {}
            }
            MemberId = maxId + 1;
        }
        Database.writeMember(MemberId, accountId, Mname , LocalDate.now().plusDays(10).format(DATE_FMT) , CoachId);
    }
}
