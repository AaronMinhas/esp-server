import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final int PORT = 8080;
    private static volatile boolean running = false;
    private static ServerSocket serverSocket = null;
    private static final Set<String> acceptedClientIds = new HashSet<>();

    static {
        System.out.println("Logger setup initiated.");
        try {
            // Create logs directory if it does not exist.
            Files.createDirectories(Paths.get("logs"));

            // Create log files with timestamp in logs directory.
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String logFileName = "logs/" + timeStamp + ".log";

            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Removes logging in console
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }

            logger.setUseParentHandlers(false);

            logger.log(Level.INFO, "Logger initialised. Logging has started.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up logger: " + e.getMessage(), e);
        }
        System.out.println("Logger setup completed."); // Console msg for successful logging startup
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
                logger.log(Level.INFO, "Server is already running");
                return;
            }
            running = true;
        }

        try {
            serverSocket = new ServerSocket(PORT);
            logger.log(Level.INFO, "Server started. Listening on port " + PORT);
            System.out.println("Server is listening on port " + PORT);
            System.out.println("Available commands: start, stop, restart, exit");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected.");
                    logger.log(Level.INFO, "Client connected.");

                            new Thread(() -> processClients(socket)).start();
                } catch (IOException e) {
                    if (running) {
                        logger.log(Level.SEVERE, "Error accepting client connection: " + e.getMessage(), e);
                    }
                }
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server exception: " + ex.getMessage(), ex);
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing server socket: " + e.getMessage(), e);
            }
            synchronized (Main.class) {
                running = false;
            }
        }
    }

    private static void processClients(Socket socket) {
        // TODO:
        // Parse JSON for acceptedClientIds and redirect to appropriate classes.
        // Add class for C1 mail monitor with required functionality.
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
            logger.log(Level.SEVERE, "Error reading command: " + e.getMessage(), e);
        }
    }

    private static void stopServer() {
        synchronized (Main.class) {
            if (!running) {
                System.out.println("Server is offline.");
                return;
            }
            System.out.println("Server stopped.");
            logger.log(Level.INFO, "Server stopped.");
                    running = false;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing server socket: " + e.getMessage(), e);
            }
        }
    }

    private static void restartServer() {
        System.out.println("Restart initiated.");
        logger.log(Level.INFO, "Restart initiated.");
        stopServer();
        new Thread(Main::startSocketServer).start();
    }

    private static void exitServer() {
        System.out.println("Exit initiated.");
        stopServer();
        logger.log(Level.INFO, "Server shutdown.");
        System.exit(0);
    }
}
