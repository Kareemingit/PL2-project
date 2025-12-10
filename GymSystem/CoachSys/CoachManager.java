package GymSystem.CoachSys;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import GymSystem.Database;
import GymSystem.Account;
import GymSystem.Account.SRole;
public class CoachManager {
    private Coach crrntCoach;
    public CoachManager(Coach crrntCoach){
        this.crrntCoach=crrntCoach;
    }
}
