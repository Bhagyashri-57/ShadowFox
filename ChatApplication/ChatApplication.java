import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class ChatApplication {

    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println(" Chat Server Started Successfully ");
        System.out.println(" Running on Port: " + PORT);
        System.out.println("=================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected: "
                        + socket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(socket);

                synchronized (clients) {
                    clients.add(clientHandler);
                }

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}


class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String userName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            output = new PrintWriter(
                    socket.getOutputStream(), true);

            output.println("Enter your username:");

            userName = input.readLine();

            System.out.println(userName + " joined the chat.");

            ChatApplication.broadcastMessage(
                    "🔔 " + userName + " joined the chat.",
                    this
            );

            String clientMessage;

            while ((clientMessage = input.readLine()) != null) {

                if (clientMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                String finalMessage =
                        "[" + userName + "]: " + clientMessage;

                System.out.println(finalMessage);

                ChatApplication.broadcastMessage(
                        finalMessage,
                        this
                );
            }

        } catch (IOException e) {
            System.out.println("Connection closed for user: " + userName);
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    private void closeConnection() {
        try {
            if (userName != null) {
                System.out.println(userName + " left the chat.");

                ChatApplication.broadcastMessage(
                        "❌ " + userName + " left the chat.",
                        this
                );
            }

            ChatApplication.removeClient(this);

            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();

        } catch (IOException e) {
            System.out.println("Error closing connection.");
        }
    }
}
