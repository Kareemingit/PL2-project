package GymSystem.MemberSys;
import java.time.LocalDate;
import GymSystem.Account;

public class Member extends Account{
    private int MemberId;
    private LocalDate subscriptionEnd;
    private int coachId;
    public Member(int id, String username, String password, SRole role,
               String name, String email, String phone , int Mid , LocalDate se , int Cid){
            super(id , username , password , SRole.MEMBER , name , email , phone);
            this.MemberId = Mid;
            this.coachId = Cid;
    }
    public int getMemberId() {
        return MemberId;
    }

    public LocalDate getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public int getCoachId() {
        return coachId;
    }
}
