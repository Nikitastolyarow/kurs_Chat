import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;

public class ServerTest {

    private static final int PORT = 8088;  // Порт сервера
    private static final String HOST = "127.0.0.1";  // Адрес сервера

    @Test
    public void testMultipleUsers() throws InterruptedException {
        int numberOfUsers = 10;
        Thread[] threads = new Thread[numberOfUsers];  // Массив потоков

        // Создаем и запускаем потоки для каждого пользователя
        for (int i = 0; i < numberOfUsers; i++) {
            final int userId = i + 1;
            threads[i] = new Thread(() -> connectAndSendMessage(userId));
            Thread.sleep(500);
            threads[i].start();
        }

        // Используем join для каждого потока, чтобы дождаться их завершения
        for (Thread thread : threads) {
            try {
                thread.join();  // Ожидаем завершения потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectAndSendMessage(int userId) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Формируем сообщение
            String message1 = "testUser" + userId ;
            String message2 = " Hello, my number is -> " + userId;
            String message3= "exit";

            // Отправляем сообщение
            out.println(message1);
            Thread.sleep(100);
            out.println(message2);
            Thread.sleep(100);
            out.println(message3);
            Thread.sleep(100);
            // Читаем ответ
            String response = in.readLine();
            System.out.println("A response was received from the server " + response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
