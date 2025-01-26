import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 50852;
    private static final Map<String, ClientHandler> activeConnections = new ConcurrentHashMap<>();
    private static int clientCounter = 0;

    public static void main(String[] args) {
        System.out.println("[SERVER] Сервер запущений...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (Server.class) {
                    clientCounter++;
                }
                String clientName = "client-" + clientCounter;
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientName);
                activeConnections.put(clientName, clientHandler);
                System.out.println("[SERVER] " + clientName + " успішно підключився.");
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Помилка: " + e.getMessage());
        }
    }

    static void removeClient(String clientName) {
        activeConnections.remove(clientName);
        System.out.println("[SERVER] " + clientName + " відключився.");
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String clientName;

        public ClientHandler(Socket clientSocket, String clientName) {
            this.clientSocket = clientSocket;
            this.clientName = clientName;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("[SERVER] Ви підключені як " + clientName);
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.trim().equalsIgnoreCase("exit")) {
                        break;
                    }
                    System.out.println("[" + clientName + "] " + message);
                }
            } catch (IOException e) {
                System.err.println("[SERVER] Помилка з'єднання з " + clientName);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("[SERVER] Помилка закриття сокету для " + clientName);
                }
                Server.removeClient(clientName);
            }
        }
    }
}
