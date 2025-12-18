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
                if (accountData.length >= 7) {
                for (String data : accountData) {
                    record.add(data.trim());
                }

                accounts.add(record);}
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
        if (!Files.exists(p)) return coaches;
        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] coachData = line.split(",");
                if (coachData.length >= 4) {
                    ArrayList<String> record = new ArrayList<>();


                for (String data : coachData) {
                    record.add(data.trim());
                }
                coaches.add(record);}
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
                String[] memberData = line.split(",");
                ArrayList<String> record = new ArrayList<>();
                for (String data : memberData) {
                    record.add(data.trim());
                }
                members.add(record);
            }
        } catch (IOException e) { e.printStackTrace(); }
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
    public static boolean checkIfIdExistsInFile(int id, String fileName) {
        Path p = DATA_DIR.resolve(fileName);
        if (!Files.exists(p)) return false;

        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split(",");
                try {
                    int existingId = Integer.parseInt(parts[0].trim());
                    if (existingId == id) {
                        return true;
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    public static void updateAccount(int id, String un, String pw, String role, String name, String email, String phone) {
        ArrayList<ArrayList<String>> accounts = readAccounts();
        ArrayList<String> lines = new ArrayList<>();

        for (ArrayList<String> a : accounts) {
            if (Integer.parseInt(a.get(0).trim()) == id) {
                // Write the updated data back
                lines.add(id + "," + un + "," + pw + "," + role + "," + name + "," + email + "," + phone);
            } else {
                lines.add(String.join(",", a));
            }
        }
        try {
            Files.write(DATA_DIR.resolve("Account.csv"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccountById(int id) {
        ArrayList<ArrayList<String>> accounts = readAccounts();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> a : accounts) {
            if (Integer.parseInt(a.get(0).trim()) != id)
                lines.add(String.join(",", a));
        }
        try { Files.write(DATA_DIR.resolve("Account.csv"), lines); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static boolean accountExists(int id) {
        return readAccounts().stream().anyMatch(a -> Integer.parseInt(a.get(0)) == id);
    }

    public static void deleteMemberById(int id) {
        ArrayList<ArrayList<String>> members = readMembers();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> m : members) {
            if (Integer.parseInt(m.get(0).trim()) != id) {
                lines.add(String.join(",", m));
            }
        }
        try {
            Files.write(DATA_DIR.resolve("members.csv"), lines);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void deleteCoachById(int id) {
        ArrayList<ArrayList<String>> coaches = readCoachs();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> c : coaches) {
            if (Integer.parseInt(c.get(0).trim()) != id) {
                lines.add(String.join(",", c));
            }
        }
        try {
            Files.write(DATA_DIR.resolve("coaches.csv"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateMember(int mid, int aid, String name, String date, int coachId) {
        ArrayList<ArrayList<String>> members = readMembers();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> m : members) {
            if (Integer.parseInt(m.get(0).trim()) == mid) {
                lines.add(mid + "," + aid + "," + escape(name) + "," + escape(date) + "," + coachId);
            } else {
                lines.add(String.join(",", m));
            }
        }
        try { Files.write(DATA_DIR.resolve("members.csv"), lines); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static void updateCoach(int cid, int aid, String name, String spec) {
        ArrayList<ArrayList<String>> coaches = readCoachs();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> c : coaches) {
            if (Integer.parseInt(c.get(0).trim()) == cid) {
                lines.add(cid + "," + aid + "," + escape(name) + "," + escape(spec));
            } else {
                lines.add(String.join(",", c));
            }
        }
        try { Files.write(DATA_DIR.resolve("coaches.csv"), lines); } catch (IOException e) { e.printStackTrace(); }
    }
    // Add these to your Database class to make the Admin system fully functional

    public static void unassignCoachFromMembers(int coachId) {
        ArrayList<ArrayList<String>> members = readMembers();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> m : members) {
            // If the member is assigned to this coach, set coachId (index 4) to 0
            if (m.size() > 4 && Integer.parseInt(m.get(4).trim()) == coachId) {
                m.set(4, "0");
            }
            lines.add(String.join(",", m));
        }
        try {
            Files.write(DATA_DIR.resolve("members.csv"), lines);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static ArrayList<ArrayList<String>> readBillings() {
        Path p = DATA_DIR.resolve("billing.csv");
        ArrayList<ArrayList<String>> billings = new ArrayList<>();
        if (!Files.exists(p)) return billings;

        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                ArrayList<String> record = new ArrayList<>(Arrays.asList(line.split(",")));
                billings.add(record);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return billings;
    }

    public static void writeBilling(int id, int mid, double amt, String date, String desc) {
        Path p = DATA_DIR.resolve("billing.csv");
        String row = String.format(java.util.Locale.US, "%d,%d,%.2f,%s,%s",
                id, mid, amt, escape(date), escape(desc));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString(), true))) {
            writer.write(row);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to billing.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteBillingById(int id) {
        ArrayList<ArrayList<String>> billings = readBillings();
        ArrayList<String> lines = new ArrayList<>();
        for (ArrayList<String> b : billings) {
            if (Integer.parseInt(b.get(0).trim()) != id) {
                lines.add(String.join(",", b));
            }
        }
        try {
            Files.write(DATA_DIR.resolve("billing.csv"), lines);
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static int generateRandomUniqueId(String fileName) {
        int randomId;
        boolean exists;
        do {
            randomId = (int)(Math.random() * 10000) + 1;

            exists = checkIfIdExistsInFile(randomId, fileName);
        } while (exists);

        return randomId;
    }
}