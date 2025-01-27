import java.io.*;
import java.net.*;

/**
 * This class represents a client of the system.
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
            for (int i=2; i<args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
//            out.println(stringBuilder.toString());
            out.println("5 2 1");
//            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            String line;
            int lineCount = 0;
            while ((line = in.readLine()) != null) {
                if (line.equals("null")) { // Check for the end signal
                    break;
                }
                System.out.print(line + "\n");
                lineCount++;
            }

//            for (int i = 0; i < lineCount; i++) {
//                System.out.print("\033[F"); // Move the cursor up one line
//                System.out.print("\r\033[K"); // Clear the current line
//            }

//            System.out.println(in.lines());
//            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
