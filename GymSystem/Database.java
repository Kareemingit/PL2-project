package GymSystem;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import GymSystem.Account.SRole;
import GymSystem.CoachSys.*;
import GymSystem.MemberSys.*;

public class Database {
    private static final Path DATA_DIR = Paths.get("data");

    private static String[] splitCSV(String line) {
        String[] parts = line.split(",");
        
        for (int i=0;i<parts.length;i++) 
            parts[i]=parts[i].trim();
        return parts;
    }

    private static String escape(String s) {
        if (s==null) return "";
        // naive escape: replace newlines and commas
        return s.replace("\n"," ").replace("\r"," ").replace(",",";");
    }

    public static ArrayList<Account> readAccounts(){
        Path p = DATA_DIR.resolve("Account.csv");
        ArrayList<Account> accounts = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] f = splitCSV(line);
                int id = Integer.parseInt(f[0]);
                String username = f[1] , password = f[2];
                SRole role = Account.SRole.valueOf(f[3].trim().toUpperCase());
                String name = f.length > 4 ? f[4] : "";
                String email = f.length > 5 ? f[5] : "";
                String phone = f.length > 6 ? f[6] : "";
                accounts.add(new Account(id, username, password, role, name, email, phone));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }
    
    public static void writeAccount(Account account){
        Path p = DATA_DIR.resolve("Account.csv");
        int accId = account.id;
        if(account.id == -1){
            ArrayList<Account> accounts = readAccounts();
            int maxId = accounts.stream().mapToInt(a->a.getId()).max().orElse(0);
            accId = maxId + 1;
        }
        String[] header = { String.valueOf(accId), escape(account.username), 
            escape(account.password), account.role.name(), escape(account.name), escape(account.email), escape(account.phone)};
        
        try { 
            BufferedWriter writer = new BufferedWriter(new FileWriter(p.toString()));
            writer.write(String.join(",", header));
            writer.newLine();
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static ArrayList<Coach> readCoachs(){
        return null;
    }

    public static void writeCoach(Coach coach){
    }

    public static ArrayList<Member> readMembers(){
        return null;
    }

    public static void writeMember(Member member){
    }
}
