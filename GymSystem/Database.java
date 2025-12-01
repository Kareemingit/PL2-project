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

    private static String escape(String s) {
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
        if (id < 0) {
            ArrayList<ArrayList<String>> all = readAccounts();
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
        return null;
    }

    public static void writeCoach(){
    }

    public static ArrayList<ArrayList<String>> readMembers(){
        return null;
    }

    public static void writeMember(){
    }


}
