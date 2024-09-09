import log.logServ;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {
    private static int port;
    public static void main(String[] args) {
        loadsettings();
        System.out.println("Старт сервера с портом " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.printf("Новый пользователь! Порт подключения -> %d%n ", clientSocket.getPort());
                    final String name = in.readLine();
                    out.println(String.format("Привет %s!,Твой порт подключения: %d", name, clientSocket.getPort()));
                    logServ.logLogin(name, "Присоеденился к чату" );
                    String message;
                    while ((message= in.readLine()) !=null) {
                        logServ.logLogin(name,message);
                        System.out.printf("%s: %s%n", name, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
    private static void loadsettings()  {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))){
            properties.load(reader);
            port= Integer.parseInt(properties.getProperty("port"));
            // System.out.println("Port: " + port);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке файла настроек: " + e.getMessage());
        }
    }
}
