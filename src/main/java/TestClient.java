import java.io.*;
import java.net.Socket;

// Simple placeholder TestClient for testing purposes.

public class TestClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Format message with clientId to the server (change for testing purposes)
            String jsonMessage = "{\"clientId\":\"C1\"}";

            // Send message
            writer.println(jsonMessage);

            // Read and print response from server
            String response = reader.readLine();
            System.out.println("Server response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}