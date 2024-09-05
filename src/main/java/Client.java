import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8088;

        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Привет, введи свое имя для подлючения к чату");
            String name = scanner.nextLine();
            out.println(name);
            System.out.println(in.readLine());

            while (true){
                System.out.println("Введи сообщение или end для выхода из чата");
                String message = scanner.nextLine();
                if(message.toLowerCase().equals("end"))
                    break;
                    out.println(message);
            }
            System.out.println("Вы успешно вышли из чата");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}