import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Main {
    private static final int PORT = 8080;
    private static volatile boolean running = false;
    private static ServerSocket serverSocket = null;
    private static final Set<String> acceptedClientIds = new HashSet<>();

    static {
        acceptedClientIds.add("C1");
    }

    public static void main(String[] args) {
        System.out.println("Server control interface started. Type 'start' to start the server.");
        new Thread(Main::commandInterface).start();
    }

    public static void startSocketServer() {
        // test in cli with telnet localhost 8080
        synchronized (Main.class) {
            if (running) {
                System.out.println("Server is already running");
                return;
            }
            running = true;
        }

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);
            System.out.println("Available commands: start, stop, restart, exit");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected.");

                    new Thread(() -> processClients(socket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error accepting client connection: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (Main.class) {
                running = false;
            }
        }
    }

    private static void processClients(Socket socket) {
        // TODO:
        // Parse JSON for acceptedClientIds and redirect to appropriate classes.
        // Add class for C1 mailmonitor with required functionality.
        // Expand CLI commands after above is implemented.
    }

    private static void commandInterface() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            while (true) {
                command = consoleReader.readLine().trim().toLowerCase();

                switch (command) {
                    case "start":
                        new Thread(Main::startSocketServer).start();
                        break;
                    case "stop":
                        stopServer();
                        break;
                    case "restart":
                        restartServer();
                        break;
                    case "exit":
                        exitServer();
                        return;
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopServer() {
        synchronized (Main.class) {
            if (!running) {
                System.out.println("Server is offline.");
                return;
            }
            System.out.println("Server stopped.");
            running = false;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void restartServer() {
        System.out.println("Restart initiated.");
        stopServer();
        new Thread(Main::startSocketServer).start();
    }

    private static void exitServer() {
        System.out.println("Exit initiated.");
        stopServer();
        System.exit(0);
    }
}
