import java.io.*;
import java.net.*;
import javax.swing.*;

public class chat___server extends JFrame {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private JTextArea chatArea = new JTextArea(15, 30);
    private JTextField inputField = new JTextField(25);
    private JLabel statusLabel = new JLabel("Status: Not Connected");
    private int port = 8880;
    public chat___server() {
        setTitle("Chat Server");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());
        add(new JScrollPane(chatArea), java.awt.BorderLayout.CENTER);
        add(inputField, java.awt.BorderLayout.SOUTH);
        add(new JButton("Send") {{
            addActionListener(e -> sendMessage(inputField.getText()));
        }}, java.awt.BorderLayout.EAST);
        add(statusLabel, java.awt.BorderLayout.NORTH);
        chatArea.setEditable(false);
        pack();
        setVisible(true);
        startServer();
    }
    private void sendMessage(String message) {
        try {
            chatArea.append("Me: " + message + "\n");
            output.writeObject("Server: " + message);
            output.flush();
            inputField.setText("");
        } catch (IOException e) {
            chatArea.append("Failed to send message\n");
        }
    }
    public void startServer() {
        try (ServerSocket server = new ServerSocket(port)) {
            statusLabel.setText("Waiting for client to connect...");
            while (true) {
                try (Socket connection = server.accept()) {
                    statusLabel.setText("Connected to: " + connection.getInetAddress().getHostName());
                    output = new ObjectOutputStream(connection.getOutputStream());
                    input = new ObjectInputStream(connection.getInputStream());
                    String message;
                    while ((message = (String) input.readObject()) != null) {
                        chatArea.append(message + "\n");
                    }
                } catch (EOFException e) {
                    statusLabel.setText("Client disconnected");
                } catch (IOException | ClassNotFoundException e) {
                    chatArea.append("Connection closed or error occurred\n");
                }
            }
        } catch (IOException e) {
            statusLabel.setText("Error starting server: " + e.getMessage());
        }
    }
}
