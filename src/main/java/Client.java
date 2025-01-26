import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 50852;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readLine()); // Привітання від сервера
            String userInput;
            while (true) {
                System.out.print("> ");
                userInput = console.readLine();
                out.println(userInput);
                if (userInput.trim().equalsIgnoreCase("exit")) {
                    System.out.println("[CLIENT] Відключення...");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] Помилка: " + e.getMessage());
        }
    }
}
