package GymSystem;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.CoachSys.MemberPlan;
import GymSystem.CoachSys.Message;

public class Database {
    private static final Path DATA_DIR = Paths.get("data");

    private static String[] splitCSV(String line) {
        String[] parts = line.split(",");
        
        for (int i=0;i<parts.length;i++) 
            parts[i]=parts[i].trim();
        return parts;
    }

    public static String escape(String s) {
        if (s==null) return "";
        return s.replace("\n"," ").replace("\r"," ").replace(",",";");
    }

    public static ArrayList<ArrayList<String>> readAccounts() {
        Path p = DATA_DIR.resolve("Account.csv");
        ArrayList<ArrayList<String>> accounts = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                ArrayList<String> record = new ArrayList<>();
                String[] accountData = line.split(",");

                for (String data : accountData) {
                    record.add(data.trim());
                }

                accounts.add(record);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return accounts;
    }
    
    public static void writeAccount(int id, String username, String password, SRole role,
                                    String name, String email, String phone) {
        Path p = DATA_DIR.resolve("Account.csv");
        String[] row = {
            String.valueOf(id),
            escape(username),
            escape(password),
            role.name(),
            escape(name),
            escape(email),
            escape(phone)
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString(), true))) {
            writer.write(String.join(",", row));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> readCoachs(){
        Path p = DATA_DIR.resolve("coaches.csv");
        ArrayList<ArrayList<String>> coaches = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                ArrayList<String> record = new ArrayList<>();
                String[] coachData = line.split(",");
                for (String data : coachData) {
                    record.add(data.trim());
                }
                coaches.add(record);
            }

        } catch (IOException e) {e.printStackTrace();}

        return coaches;
    }

    public static void writeCoach(int CoachId , int accountID, String name, String specialty){
        Path p = DATA_DIR.resolve("coaches.csv");

        String[] row = {
            String.valueOf(CoachId),
            String.valueOf(accountID),
            escape(name),
            escape(specialty)
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString(), true))) {
            writer.write(String.join(",", row));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> readMembers(){
        Path p = DATA_DIR.resolve("members.csv");
        ArrayList<ArrayList<String>> members = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                ArrayList<String> record = new ArrayList<>();
                String[] memberData = line.split(",");
                for (String data : memberData) {
                    record.add(data);
                }
                members.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return members;
    }

    public static void writeMember(int MemberId , int accountId , String name, String endData , int CoachId){
        Path p = DATA_DIR.resolve("members.csv");
        String[] row = {
            String.valueOf(MemberId),
            String.valueOf(accountId),
            escape(name),
            escape(endData),
            String.valueOf(CoachId)
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString(), true))) {
            writer.write(String.join(",", row));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> findAccountByUsername(String username) {
        return readAccounts().stream().filter(u -> u.size() > 1 && u.get(1).equals(username)).findFirst().orElse(null);
    }

    public static void writeMemberPlan(MemberPlan plan) {
        Path p = Database.DATA_DIR.resolve("schedules.csv");
        int id = plan.getPlanId();

        try {
            if (id <= 0) {
                int maxId = 0;
                if (Files.exists(p)) {
                    List<String> lines = Files.readAllLines(p);
                    for (String line : lines) {
                        if (line.trim().isEmpty()) continue;
                        try {
                            int rowId = Integer.parseInt(line.split(",")[0].trim());
                            if (rowId > maxId) maxId = rowId;
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}
                    }
                }
                id = maxId + 1;
                plan.setPlanId(id);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toFile(), true))) {
                writer.write(plan.objToCsv());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing plan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void writeMessage(Message message) {
        Path p = Database.DATA_DIR.resolve("messages.csv");
        int id = message.getMssgId();

        try {
            if (id <= 0) {
                int maxId = 0;
                if (Files.exists(p)) {
                    List<String> lines = Files.readAllLines(p);
                    for (String line : lines) {
                        if (line.trim().isEmpty()) continue;
                        try {
                            int rowId = Integer.parseInt(line.split(",")[0].trim());
                            if (rowId > maxId) maxId = rowId;
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}
                    }
                }
                id = maxId + 1;
                message.setMssgId(id);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toFile(), true))) {
                writer.write(message.toCSVstring());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
