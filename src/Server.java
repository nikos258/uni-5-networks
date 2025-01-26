import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;

public class Server {
    private static List<Account> accountList = Collections.synchronizedList(new ArrayList<>());
    private static final SecureRandom secureRandom = new SecureRandom();
    private static int nextMessageId = 1;

    private static void initialise() { //todo del
        accountList.add(new Account("user1", 1));
        accountList.add(new Account("user2", 2));
        accountList.add(new Account("user3", 3));
    }

    public static void main(String[] args) {
        initialise();
        System.out.println("Initialise"); //todo: del
        int port = Integer.parseInt(args[0]);

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);

            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());//todo: del

                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);)
            {
                String request = in.readLine();

                System.out.println(request); //todo: del

                String response = handleRequest(request);

                System.out.println(response); //todo: del

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private String handleRequest(String request) {
            String[] parts = request.split(" ");
            int fn_id = Integer.parseInt(parts[0]);
            String response = null;

            if (fn_id == 1) {
                String username = parts[1];
                response = createAccount(username);
            } else {
                int authtoken = Integer.parseInt(parts[1]);
                if (! authTokenExists(authtoken))
                    response = "Invalid Auth Token";

                switch (fn_id) {
                    case 2:
                        response = showAccounts();
                        break;
                    case 3:
                        StringBuilder message = new StringBuilder();
                        for (int i=3; i < parts.length; i++) {
                            message.append(parts[i]);
                        }
                        response = sendMessage(findAccountFromAuthToken(authtoken).getUsername(), parts[2], message.toString());
                        break;
                    case 4:
                        response = showInbox(findAccountFromAuthToken(authtoken).getUsername()); //todo make while loop

                }
            }

            return response;
        }

        private boolean usernameExists(String username) {
            for (Account account: accountList) {
                if (account.getUsername().equals(username))
                    return true;
            }
            return false;
        }

        private boolean authTokenExists(int authToken) {
            for (Account account: accountList) {
                if (account.getAuthToken() == authToken)
                    return true;
            }
            return false;
        }

        private Account findAccountFromAuthToken(int authToken) {
            for (Account account: accountList) {
                if (account.getAuthToken() == authToken)
                    return account;
            }
            return null;
        }

        private Account findAccountFromUsername(String username) {
            for (Account account: accountList)
                if (account.getUsername().equals(username))
                    return account;
            return null;
        }

        private synchronized String createAccount(String username) {
            // checks if the username is empty or contains invalid characters
            if (username.isEmpty() || Pattern.compile("[^A-Za-z0-9]").matcher(username).find())
                return "Invalid Username";

            if (usernameExists(username))
                return "Sorry, the user already exists";

            // produces a new authentication token
            int authToken = Math.abs(secureRandom.nextInt());
            while (authTokenExists(authToken)) {
                authToken = Math.abs(secureRandom.nextInt());
            }

            Account account = new Account(username, authToken);
            accountList.add(account);
            return String.valueOf(authToken);
        }

        private String showAccounts() {
            StringBuilder response = new StringBuilder();

            int i = 1;
            for (Account account: accountList) {
                response.append(i++).append(". ");
                response.append(account.getUsername());
                response.append("\n");
            }
            response.deleteCharAt(response.length()-1);

            return response.toString();
        }

        private synchronized String sendMessage(String sender, String recipient, String message_body) {
            if (!usernameExists(recipient))
                return "User does not exist";

            Message message = new Message(nextMessageId++, sender, recipient, message_body);
            Account recipientAccount = findAccountFromUsername(recipient);
            assert recipientAccount != null;
            recipientAccount.addToMessageBox(message);
            return "OK";
        }

        private String showInbox(String username) {
            Account account = findAccountFromUsername(username);
            return account.getAllMessages();
        }

        private String readMessage(String username, int messageId) {
            String error = "Message ID does not exist";

            Account account = findAccountFromUsername(username);
            if (account == null)
                return error;

            Message message = account.getMessageFromId(messageId);
            if (message == null)
                return error;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(").append(message.getSender()).append(") ").append(message.getBody());
            return stringBuilder.toString();
        }

        private String deleteMessage(String username, int messageId) {
            String error = "Message does not exist";

            Account account = findAccountFromUsername(username);
            if (account == null)
                return error;

            if (account.deleteMessage(messageId))
                return "OK";
            return error;
        }
//        public void run()
//        {
//            PrintWriter out = null;
//            BufferedReader in = null;
//            try {
//                // get the outputstream of client
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//
//                // get the inputstream of client
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                // writing the received message from client
//                String line;
//                String[] arr = null;
//                while (in.readLine() != null) {
////                    System.out.println(line);
//                    line = in.readLine();
//                    arr = line.split(" ");
//                    for (String s : arr) {
//                        System.out.println(s);
//                        out.println(s);
//                        out.flush();
//                    }
//                }
//                System.out.println(arr[0]);
//                switch (arr[0]) {
//                    case "0": break; //TODO define error case
//                    case "1":
//                        System.out.println(createAccount(arr[1]));
//                        break;
//
//                }
//                    System.out.println(showAccounts());
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
