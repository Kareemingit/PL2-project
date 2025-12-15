package GymSystem;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import GymSystem.Account.SRole;

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

    public static boolean accountExists(int accountId) {
        return readAccounts().stream()
                .anyMatch(a -> Integer.parseInt(a.get(0)) == accountId);
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

    public static void updateAccount(int id, String username, String password, SRole role,String name, String email, String phone) {
        Path p = DATA_DIR.resolve("Account.csv");
        ArrayList<ArrayList<String>> all = readAccounts();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) == id) {
                    row.set(1, escape(username));
                    row.set(2, escape(password));
                    row.set(3, role.name());
                    row.set(4, escape(name));
                    row.set(5, escape(email));
                    row.set(6, escape(phone));
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateCoach(int coachId, int accountId, String name, String specialty) {

        Path p = DATA_DIR.resolve("coaches.csv");
        ArrayList<ArrayList<String>> all = readCoachs();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) == coachId) {
                    row.set(1, String.valueOf(accountId));
                    row.set(2, escape(name));
                    row.set(3, escape(specialty));
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccountById(int accountId) {
        Path p = DATA_DIR.resolve("Account.csv");
        ArrayList<ArrayList<String>> all = readAccounts();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) != accountId) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMemberByAccountId(int accountId) {
        Path p = DATA_DIR.resolve("members.csv");
        ArrayList<ArrayList<String>> all = readMembers();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(1)) != accountId) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCoachByAccountId(int accountId) {
        int coachId = -1;
        for (ArrayList<String> c : readCoachs()) {
            if (Integer.parseInt(c.get(1)) == accountId) {
                coachId = Integer.parseInt(c.get(0));
                break;
            }
        }
        Path cp = DATA_DIR.resolve("coaches.csv");
        try (BufferedWriter w = Files.newBufferedWriter(cp)) {
            for (ArrayList<String> c : readCoachs()) {
                if (Integer.parseInt(c.get(1)) != accountId) {
                    w.write(String.join(",", c));
                    w.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (coachId >= 0) {
            Path mp = DATA_DIR.resolve("members.csv");
            try (BufferedWriter w = Files.newBufferedWriter(mp)) {
                for (ArrayList<String> m : readMembers()) {
                    if (Integer.parseInt(m.get(4)) == coachId) {
                        m.set(4, "0");
                    }
                    w.write(String.join(",", m));
                    w.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateMember(int memberId, int accountId,String name, String endDate, int coachId) {
        Path p = DATA_DIR.resolve("members.csv");
        ArrayList<ArrayList<String>> all = readMembers();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) == memberId) {
                    row.set(1, String.valueOf(accountId));
                    row.set(2, escape(name));
                    row.set(3, escape(endDate));
                    row.set(4, String.valueOf(coachId));
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMemberById(int memberId) {
        Path p = DATA_DIR.resolve("members.csv");
        ArrayList<ArrayList<String>> all = readMembers();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) != memberId) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCoachById(int coachId) {
        Path p = DATA_DIR.resolve("coachs.csv");
        ArrayList<ArrayList<String>> all = readCoachs();
        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> row : all) {
                if (Integer.parseInt(row.get(0)) != coachId) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unassignCoachFromMembers(int coachId) {
        Path p = DATA_DIR.resolve("members.csv");
        ArrayList<ArrayList<String>> members = readMembers();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (ArrayList<String> m : members) {

                if (Integer.parseInt(m.get(4)) == coachId) {
                    m.set(4, "0");
                }

                writer.write(String.join(",", m));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBilling(int billingID , int MemberID , double amount , String date , String note){
        Path p = DATA_DIR.resolve("billing.csv");
        String[] row = {
            String.valueOf(billingID),
            String.valueOf(MemberID),
            String.valueOf(amount),
            escape(date),
            escape(note)
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString(), true))){
            writer.write(String.join(",", row));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> readBillings(){
        Path p = DATA_DIR.resolve("billing.csv");
        ArrayList<ArrayList<String>> billings = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                ArrayList<String> record = new ArrayList<>();
                String[] billingData = line.split(",");
                for (String data : billingData) {
                    record.add(data.trim());
                }
                billings.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return billings;
    }

    public static void deleteBillingById(int billingId) {
        Path p = DATA_DIR.resolve("billing.csv");
        ArrayList<ArrayList<String>> billings = readBillings();

        try (BufferedWriter writer = Files.newBufferedWriter(p)) {

            for (ArrayList<String> b : billings) {
                if (Integer.parseInt(b.get(0)) != billingId) {
                    writer.write(String.join(",", b));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}