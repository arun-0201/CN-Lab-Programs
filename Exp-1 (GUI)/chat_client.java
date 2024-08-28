import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class chat_client extends JFrame {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel statusLabel;
    private Socket connection;
    private String serverIP = "127.0.0.1";
    private int port = 8880;

    public chat_client(String serverIP) {
        this.serverIP = serverIP;
        initComponents();
    }

    private void initComponents() {
        chatArea = new JTextArea(15, 30);
        inputField = new JTextField(25);
        sendButton = new JButton("Send");
        statusLabel = new JLabel("Status: Not Connected");

        chatArea.setEditable(false);
        sendButton.addActionListener(e -> sendMessage(inputField.getText()));

        setLayout(new java.awt.BorderLayout());
        add(new JScrollPane(chatArea), java.awt.BorderLayout.CENTER);
        add(inputField, java.awt.BorderLayout.SOUTH);
        add(sendButton, java.awt.BorderLayout.EAST);
        add(statusLabel, java.awt.BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true); // Ensure the frame is visible
    }

    private void sendMessage(String message) {
        try {
            chatArea.append("Me: " + message + "\n");
            output.writeObject("Client: " + message);
            output.flush();
            inputField.setText("");
        } catch (IOException e) {
            chatArea.append("Failed to send message\n");
        }
    }

    public void startRunning() {
        try {
            statusLabel.setText("Connecting...");
            connection = new Socket(InetAddress.getByName(serverIP), port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());

            statusLabel.setText("Connected to: " + connection.getInetAddress().getHostName());
            receiveMessages();
        } catch (IOException e) {
            statusLabel.setText("Connection failed: " + e.getMessage());
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = (String) input.readObject()) != null) {
                chatArea.append(message + "\n");
            }
        } catch (IOException | ClassNotFoundException e) {
            chatArea.append("Connection closed or error occurred\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            chat_client client = new chat_client("127.0.0.1");
            client.startRunning(); // Start client after GUI is set up
        });
    }
}
