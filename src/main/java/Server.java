import log.logServ;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {
    private static int port;
    private static final List<PrintWriter> clients = new ArrayList<>();
    public static void main(String[] args) {
        loadsettings();
        System.out.println("Старт сервера с портом " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    synchronized (clients) {
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     clients.add(out);
                     clientHandler(clientSocket,out);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при запуске клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
    private static void clientHandler(Socket clientSocket , PrintWriter out){
            new Thread(() -> {
                try ( BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
                    System.out.printf("Новый пользователь! Порт подключения -> %d%n ", clientSocket.getPort());
                    String name = in.readLine();
                    out.println(String.format("Привет %s!,Твой порт подключения: %d", name, clientSocket.getPort()));
                    logServ.logLogin(name, "Присоеденился к чату");
                    String message;
                    while ((message = in.readLine()) != null) {
                        logServ.logLogin(name, message);
                        System.out.printf("%s: %s%n", name, message);
                    }
                    logServ.logLogin(name,"Покинул чат");
                } catch (IOException e) {
                    System.out.println("Ошибка при обработке клиента: " + e.getMessage());
                }
                finally {
                    try{
                        clientSocket.close();
                    } catch (Exception e) {
                        System.out.println("Ошибка при закрытии сокета: " + e.getMessage());
                    }
                    synchronized (clients) {
                        clients.remove(out);
                    }
                }
            }) .start();
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
