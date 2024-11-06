import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class chat_server extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private JLabel connectionLabel;
    private DataInputStream din;
    private DataOutputStream dout;
    private ServerSocket serverSocket;
    private Socket socket;

    public chat_server() {
        setTitle("Server");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connection status label
        connectionLabel = new JLabel("Waiting for connection on port 3333...");
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

        // Establish server connection
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3333);
                socket = serverSocket.accept();
                connectionLabel.setText("Client connected: " + socket.getInetAddress());
                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());

                // Thread to receive messages
                String received;
                while (!(received = din.readUTF()).equals("stop")) {
                    textArea.append("Client: " + received + "\n");
                }
            } catch (Exception ex) {
                textArea.append("Connection error.\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new chat_server().setVisible(true));
    }
}
