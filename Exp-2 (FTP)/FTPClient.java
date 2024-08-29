import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FTPClient {
    public static void main(String[] args) {
        Socket socket = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket("localhost", 3333);
            System.out.println("Connected to the server.");

            dos = new DataOutputStream(socket.getOutputStream());

            // Prompt user to enter the path of the file to send
            System.out.print("Enter the path of the file to send: ");
            String filePath = scanner.nextLine();

            // Prompt user to enter the destination path and name on the server
            System.out.print("Enter the destination path and file name to save on the server: ");
            String destinationPath = scanner.nextLine();

            // Sending the destination path and filename to the server
            dos.writeUTF(destinationPath);

            // Reading the file and sending its data
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (fis != null) fis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
