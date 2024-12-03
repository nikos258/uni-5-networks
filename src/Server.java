import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Hashtable;
import java.io.*;
import java.net.*;

public class Server {
    private List<Account> accountList = new ArrayList<>();
    private static final SecureRandom secureRandom = new SecureRandom();

    public static void main(String[] args) {
        int port = 5000; //TODO change port to args

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            while (true) {

                Socket client = server.accept();
//                System.out.println("New client connected " + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        int authToken = secureRandom.nextInt();
        while (authTokenExists(authToken)) {
            authToken = secureRandom.nextInt();
        }

        Account account = new Account(username, authToken);
        accountList.add(account);
        return String.valueOf(authToken);
    }

    private String showAccounts() {
        StringBuilder response = new StringBuilder();

        for (Account account: accountList) {
            response.append(account.getUsername());
            response.append("\n");
        }

        return response.toString();
    }

    private String sendMessage(String sender, String recipient, String message_body) {
        if (!usernameExists(recipient))
            return "User does not exist";

        Message message = new Message(sender, recipient, message_body);
        Account recipientAccount = findAccountFromUsername(recipient);
        assert recipientAccount != null;
        recipientAccount.addToMessageBox(message);
        return "OK";
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                // get the outputstream of client
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // get the inputstream of client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // writing the received message from client
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.printf(
                            " Sent from the client: %s\n",
                            line);
                    out.println(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
