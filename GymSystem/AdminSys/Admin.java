package GymSystem.AdminSys;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import GymSystem.Account;
import GymSystem.Database;

import javax.swing.*;


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

    public void addCoach(int ignoredId, int ignoredAccountId, String name, String specialty) {
        int newAccountId = Database.generateRandomUniqueId("Account.csv");
        Database.writeAccount(newAccountId, "coach_" + newAccountId, "pass123", SRole.COACH, name, "", "");
        int newCoachId = Database.generateRandomUniqueId("coaches.csv");
        Database.writeCoach(newCoachId, newAccountId, name, specialty);
    }

    public void addMember(int ignoredMid, int ignoredAid, String name, int coachId) {
        int newAccountId = Database.generateRandomUniqueId("Account.csv");

        Database.writeAccount(newAccountId, "mem_" + newAccountId, "pass123", SRole.MEMBER, name, "", "");

        int newMemberId = Database.generateRandomUniqueId("members.csv");

        String endDate = LocalDate.now().plusMonths(1).toString();
        Database.writeMember(newMemberId, newAccountId, name, endDate, coachId);
    }
    
    public void editAccount(int id, String username, String password, SRole role,String name, String email, String phone) {
        Database.updateAccount(id, username, password, role, name, email, phone);
    }

    public void deleteAccountCascade(int accountId, SRole role) {
        if (role == SRole.ADMIN) {
            JOptionPane.showMessageDialog(null, "Admin accounts cannot be deleted");
            return;
        }

        if (role == SRole.MEMBER) {
            ArrayList<ArrayList<String>> members = Database.readMembers();
            for (ArrayList<String> m : members) {
                if (m.size() > 1 && Integer.parseInt(m.get(1).trim()) == accountId) {
                    Database.deleteMemberById(Integer.parseInt(m.get(0).trim())); // Delete by Member ID
                    break;
                }
            }
        }

        if (role == SRole.COACH) {
            ArrayList<ArrayList<String>> coaches = Database.readCoachs();
            for (ArrayList<String> c : coaches) {
                if (c.size() > 1 && Integer.parseInt(c.get(1).trim()) == accountId) {
                    Database.deleteCoachById(Integer.parseInt(c.get(0).trim())); // Delete by Coach ID
                    break;
                }
            }
        }
        Database.deleteAccountById(accountId);
    }

    public void editCoach(int coachId, int accountId, String name, String specialty) {
        Database.updateCoach(coachId, accountId, name, specialty);
    }

    public void editMember(int memberId, int accountId,String name, String endDate, int coachId) {
        Database.updateMember(memberId, accountId, name, endDate, coachId);
    }

    public void deleteMemberCascade(int memberId, int accountId) {

        Database.deleteMemberById(memberId);

        if (accountId > 0 && Database.accountExists(accountId)) {
            Database.deleteAccountById(accountId);
        }
    }

    public void assignCoachToMember(int memberId, int accountId,String name, String endDate, int coachId) {
        Database.updateMember(memberId, accountId, name, endDate, coachId);
    }

    public void deleteCoachCascade(int coachId) {
        Database.unassignCoachFromMembers(coachId);
        Database.deleteCoachById(coachId);
    }

    public void addBilling(int billID ,int memberId, double amount, String date, String note) {
        ArrayList<ArrayList<String>> billings = Database.readBillings();
        int maxId = billID;
        if(billID <= 0){
            for (ArrayList<String> b : billings) {
                int id = Integer.parseInt(b.get(0));
                if (id > maxId) maxId = id;
            }
        }
        int billingId = maxId + 1;

        Database.writeBilling(
                billingId,
                memberId,
                amount,
                date,
                note
        );
    }
    public void addCoachExtended(int id, int accId, String name, String spec, String email, String phone) {
        int newAccountId = Database.generateRandomUniqueId("Account.csv");
        Database.writeAccount(newAccountId, "coach_" + newAccountId, "pass123", SRole.COACH, name, email, phone);
        int newCoachId = Database.generateRandomUniqueId("coaches.csv");
        Database.writeCoach(newCoachId, newAccountId, name, spec);
    }

    public void addMemberExtended(int mid, int aid, String name, String email, String phone, int coachId) {
        int newAccountId = Database.generateRandomUniqueId("Account.csv");
        Database.writeAccount(newAccountId, "mem_" + newAccountId, "pass123", SRole.MEMBER, name, email, phone);
        int newMemberId = Database.generateRandomUniqueId("members.csv");
        String endDate = LocalDate.now().plusMonths(1).toString();
        Database.writeMember(newMemberId, newAccountId, name, endDate, coachId);
    }

    public void deleteBilling(int billingId) {
        Database.deleteBillingById(billingId);
    }

    public void deleteCoachAndAccount(int coachId, int accId) {
        Database.deleteCoachById(coachId);
        Database.deleteAccountById(accId);
    }
    public void deleteMemberAndAccount(int memberId, int accId) {
        Database.deleteMemberById(memberId);
        Database.deleteAccountById(accId);
    }
}