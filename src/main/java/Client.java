import log.LogServ;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private static int port;
    private static String host;

    public static void main(String[] args) {
        loadsettings();
        System.out.println("Starting the server with a port " + port);  // Старт сервера с портом
        System.out.println("Starting a server with a host " + host);  // Старт сервера с хостом
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Hi, enter your name to connect to the chat");  //Привет, введи свое имя для подлючения к чату
            String name = scanner.nextLine();
            LogServ.logLogin(name, "Joined the chat");  //Присоеденился к чату
            out.println(name);
            System.out.println(in.readLine());

            while (true) {
                System.out.println("Enter a message or exit to exit the chat");  //Введи сообщение или exit для выхода из чата
                String message = scanner.nextLine();
                LogServ.logLogin(name, message);
                if (message.toLowerCase().equals("exit"))
                    break;
                out.println(message);
            }
            System.out.println("You have successfully logged out of the chat");  //Вы успешно вышли из чата
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadsettings() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            properties.load(reader);
            port = Integer.parseInt(properties.getProperty("port"));
            host = properties.getProperty("host");
            // System.out.println("Port: " + port);
        } catch (IOException e) {
            System.out.println("Error loading the settings file: " + e.getMessage());  //Ошибка при загрузке файла настроек:
        }
    }
}