import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileClientTCP {
    public static void main(String[] args) {
        // Alamat IP dan port server
        String serverAddress = "localhost";
        int serverPort = 12345;

        // Meminta pengguna untuk memasukkan path file
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan path file yang akan dikirim: ");
        String filePath = scanner.nextLine();

        try {
            // Membuat koneksi ke server
            Socket socket = new Socket(serverAddress, serverPort);

            // Mendapatkan output stream untuk mengirim data ke server
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            // Mengirimkan nama file ke server
            File file = new File(filePath);
            objectOutputStream.writeUTF(file.getName());

            // Mengirimkan konten file ke server
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                objectOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File '" + file.getName() + "' berhasil dikirim ke server.");

            // Menutup stream dan socket
            fileInputStream.close();
            objectOutputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
