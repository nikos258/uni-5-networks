import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.exit;


public class Client {
    public static void main(String[] args) {
        if (args.length < 4)
            return;

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(ip, port)) {
            // reading from server
//            // object of scanner class
//            Scanner sc = new Scanner(System.in);
//            String line = null;
//            while (!"exit".equalsIgnoreCase(line)) {
//                // reading from user
//                line = sc.nextLine();
//                // sending the user input to server
//                out.println(line);
//                out.flush();
//                // displaying server reply
//            }
//            // closing the scanner object
//            sc.close();

            // writing to server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

//            int fn_id = Integer.parseInt(args[2]);
//            switch (fn_id) {
//                case 3:
//                    if (args.length != 6)
//                        return;
//                    break;
//                case 5:
//                case 6:
//                    if (args.length != 5)
//                        return;
//                    break;
//            }
            // concats the arguments main and sends them to the server
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=2; i<args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
//            out.println(stringBuilder.toString());
            out.println("2 1");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String line;
            while ((in.readLine()) != null) {
                System.out.println(in.readLine());
            }

//            System.out.println(in.readLine());
//            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
