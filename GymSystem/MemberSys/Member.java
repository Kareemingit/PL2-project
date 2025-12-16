package GymSystem.MemberSys;
import java.time.LocalDate;
import GymSystem.Account;
import GymSystem.CoachSys.Coach;
import GymSystem.CoachSys.MemberPlan;

public class Member extends Account{
    public Member() {
        super();
        this.role = SRole.MEMBER;
    }
    private int MemberId;
    private LocalDate subscriptionEnd;
    private Coach coach;
    private MemberPlan plan;
    public Member(int id, String username, String password,
               String name, String email, String phone , int Mid , LocalDate se , Coach coach){
            super(id , username , password , SRole.MEMBER , name , email , phone);
            this.MemberId = Mid;
            this.coach = coach;
            this.subscriptionEnd = se;
    }
    public int getMemberId() {
        return MemberId;
    }

    public LocalDate getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public Coach getCoach() {
        return coach;
    }
    public void setMemberId(int MemberId) {
        if(MemberId > 0) {
            this.MemberId = MemberId;
        }
        }
    public void setSubscriptionEnd(LocalDate subscriptionEnd) {
        if(subscriptionEnd != null){
            this.subscriptionEnd = subscriptionEnd;
        }
    }
    public void setCoach(Coach coach) {
        if(coach != null){
            this.coach = coach;
        }
    }
    public boolean isSubscribtionActive(){
        return subscriptionEnd != null&&!(subscriptionEnd.isBefore(LocalDate.now()));
    }
    public void setPlan(MemberPlan plan) {
        this.plan = plan;
    }
    public MemberPlan getMySchedule(){
        return plan;
    }
    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + MemberId+
                ", name='" + name + '\'' +
                ", subscriptionEnd=" + subscriptionEnd +
                ", coach=" + (coach != null ? coach.getName() : "No coach assigned")+
                ", plan=" + (plan != null ? plan.getPlanDetails() : "No plan assigned")+
                '}';
    }
}
