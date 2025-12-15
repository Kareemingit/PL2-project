package GymSystem.CoachSys;
import java.time.LocalDate;
import GymSystem.SystemEntity;
import GymSystem.GymManagmentSystem;
import GymSystem.Database;

//====================================================
//==============تم الانتهاء============================
//====================================================

public class MemberPlan extends SystemEntity {
    private int planId;
    private int coachId;
    private int memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String planDetails;

    public MemberPlan(int planId, int coachId,int memberId,
                      LocalDate startDate,LocalDate endDate,String planDetails){
        this.planId = planId;
        this.coachId = coachId;
        this.memberId = memberId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.planDetails = planDetails;
    }

    public int getCoachId() {
        return coachId;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getPlanId() {
        return planId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getPlanDetails() {
        return planDetails;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String objToCsv(){
        return String.format("%d,%d,%d,%s,%s,%s",
                planId,coachId,memberId,GymManagmentSystem.DATE_FMT.format(startDate),
                GymManagmentSystem.DATE_FMT.format(endDate),
                Database.escape(planDetails)
        );
    }



}











