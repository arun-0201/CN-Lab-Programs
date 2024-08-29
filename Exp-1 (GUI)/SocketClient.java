import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class SocketClient extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private JLabel connectionLabel;
    private DataInputStream din;
    private DataOutputStream dout;
    private Socket socket;

    public SocketClient() {
        setTitle("Client");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connection status label
        connectionLabel = new JLabel("Connected to Server at localhost:3333");
        add(connectionLabel, BorderLayout.NORTH);

        // Text area for chat messages
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Panel for input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Button action to send message
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String message = textField.getText();
                    dout.writeUTF(message);
                    dout.flush();
                    textField.setText("");
                } catch (IOException ex) {
                    textArea.append("Error sending message.\n");
                }
            }
        });

        // Establish connection
        try {
            socket = new Socket("localhost", 3333);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Thread to receive messages
            new Thread(() -> {
                try {
                    String received;
                    while (!(received = din.readUTF()).equals("stop")) {
                        textArea.append("Server: " + received + "\n");
                    }
                } catch (IOException ex) {
                    textArea.append("Disconnected from server.\n");
                }
            }).start();

        } catch (Exception ex) {
            textArea.append("Unable to connect to server.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SocketClient().setVisible(true));
    }
}
