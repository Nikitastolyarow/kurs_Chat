package log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogServ {
    public static void logLogin(String name, String message) {
        String outputFileName = "file.log";
        try (BufferedWriter writter = new BufferedWriter(new FileWriter(outputFileName, true))) {
            String dataLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date());
            writter.write(String.format("%s, Nickname:[%s]-> %s%n", dataLog, name, message));
        } catch (IOException e) {
            System.out.println("Ошибка записи" + e.getMessage());
        }
    }
}
