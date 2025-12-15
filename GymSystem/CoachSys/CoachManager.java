package GymSystem.CoachSys;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import GymSystem.Database;
import GymSystem.Account;
import GymSystem.Account.SRole;

//====================================================
//==============تم الانتهاء============================
//====================================================

public class CoachManager {
    private Coach crrntCoach;

    public CoachManager(Coach crrntCoach) {
        this.crrntCoach = crrntCoach;
    }

    public boolean createPlanForMember(int memberAccountId, LocalDate startDate,
                                       LocalDate endDate, String planDetails) {

        if (planDetails.trim().isEmpty() || startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return false;
        }

        MemberPlan newPlan = new MemberPlan(
                0,
                crrntCoach.getId(),
                memberAccountId,
                startDate,
                endDate,
                planDetails
        );
        Database.writeMemberPlan(newPlan);
        return true;
    }

    public boolean sendMessageToAllMembers(String content) {

        if (content.trim().isEmpty()) {
            return false;
        }
        Message newMessage = new Message(
                0,
                crrntCoach.getId(),
                0,
                LocalDateTime.now(),
                content
        );
        Database.writeMessage(newMessage);
        return true;
    }

}