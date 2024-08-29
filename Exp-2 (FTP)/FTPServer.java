import java.io.*;
import java.net.*;

public class FTPServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;

        try {
            serverSocket = new ServerSocket(3333);
            System.out.println("Server is running and waiting for a connection...");

            socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress());

            dis = new DataInputStream(socket.getInputStream());

            // Receiving the destination file path and file name
            String filePath = dis.readUTF();
            System.out.println("Saving file as: " + filePath);

            // Creating a file output stream to save the file
            fos = new FileOutputStream(filePath);

            // Reading file data and saving it
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
            }

            System.out.println("File received and saved successfully.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (fos != null) fos.close();
                if (dis != null) dis.close();
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
