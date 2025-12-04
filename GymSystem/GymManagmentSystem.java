package GymSystem;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;

public class GymManagmentSystem {
    public static final Path DATA_DIR = Paths.get("data");
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        try {
            ensureDataFolderAndSampleFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
    static void ensureDataFolderAndSampleFiles() throws IOException {
        if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);

        Path account = DATA_DIR.resolve("Account.csv");

        if (!Files.exists(account)) {

            List<String> lines = new ArrayList<>();

            lines.add("1,admin,admin123,ADMIN,Administrator,admin@gym.com,0123456789");
            lines.add("2,coach1,coachpass,COACH,Ahmed Ali,coach1@gym.com,01011112222");
            lines.add("3,member1,memberpass,MEMBER,Mohamed Samir,member1@gym.com,01111112222");
            Files.write(account, lines);
        }
    }
}
