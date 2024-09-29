import log.LogServ;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {
    static int port;
    private static final List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        loadsettings();
        System.out.println(" Starting the server with a port -> " + port);  //Старт сервера с портом
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    synchronized (clients) {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        clients.add(out);
                        clientHandler(clientSocket, out);
                    }
                } catch (IOException e) {
                    System.out.println("Error when starting the client: " + e.getMessage());  //Ошибка при запуске клиента
                }
            }
        } catch (IOException e) {
            System.out.println("Error when starting the server: " + e.getMessage()); //Ошибка при запуске сервера
        }
    }

    private static void clientHandler(Socket clientSocket, PrintWriter out) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                System.out.printf("A new user! Connection port -> %d%n ", clientSocket.getPort()); //Новый пользователь! Порт подключения ->
                String name = in.readLine();
                out.println(String.format("Hello %s!,Your connection port: %d", name, clientSocket.getPort())); //Привет %s!,Твой порт подключения:
                LogServ.logLogin(name, "Joined the chat"); //Присоеденился к чату
                String message;
                while ((message = in.readLine()) != null) {
                    LogServ.logLogin(name, message);
                    System.out.printf("%s: %s%n", name, message);
                    mailing(name, message);
                    if (message.toLowerCase().equals("exit"))
                        break;
                }
                LogServ.logLogin(name, "Left the chat");  //Покинул чат
            } catch (IOException e) {
                System.out.println("Error in processing the client: " + e.getMessage()); //Ошибка при обработке клиента
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println("Error closing socket: " + e.getMessage()); //Ошибка при закрытии сокета
                }
                synchronized (clients) {
                    clients.remove(out);
                }
            }
        }).start();
    }

    private static void loadsettings() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            properties.load(reader);
            port = Integer.parseInt(properties.getProperty("port"));
            // System.out.println("Port: " + port);
        } catch (IOException e) {
            System.out.println("Error loading the settings file: " + e.getMessage()); //Ошибка при загрузке файла настроек
        }
    }

    private static void mailing(String name, String message) {
        synchronized (clients) {
            for (PrintWriter writer : clients) {
                writer.println(name + " -> " + message);
            }
        }
    }
}
