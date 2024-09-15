import log.logServ;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private static int port;
    private static String host;
    public static void main(String[] args) {
        loadsettings();
        System.out.println("Старт сервера с портом " + port);
        System.out.println("Старт сервера с хостом " + host);
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Привет, введи свое имя для подлючения к чату");
            String name = scanner.nextLine();
            logServ.logLogin(name, "Присоеденился к чату");
            out.println(name);
            System.out.println(in.readLine());

            while (true){
                System.out.println("Введи сообщение или exit для выхода из чата");
                String message = scanner.nextLine();
                logServ.logLogin(name, message);
                if(message.toLowerCase().equals("exit"))
                    break;
                    out.println(message);
            }
            System.out.println("Вы успешно вышли из чата");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void loadsettings()  {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))){
            properties.load(reader);
            port= Integer.parseInt(properties.getProperty("port"));
            host= properties.getProperty("host");
            // System.out.println("Port: " + port);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке файла настроек: " + e.getMessage());
        }
    }
}