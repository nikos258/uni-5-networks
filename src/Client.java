import java.io.*;
import java.net.*;

/**
 * This class represents a client in the system.
 */
public class Client {
    public static void main(String[] args) {
        if (args.length < 4)
            return;

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(ip, port)) {
            // writing to server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            // concats the arguments main and sends them to the server
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            if (!stringBuilder.isEmpty())
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            out.println(stringBuilder.toString());

//            out.flush();

            // reading from server
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            String line;
            int lineCount = 0;
            while ((line = in.readLine()) != null) {
                if (line.equals("null")) {
                    break;
                }
                System.out.print(line + "\n");
                lineCount++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
