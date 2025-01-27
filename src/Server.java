import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;

/**
 * This class represents the server of the system.
 */
public class Server {
    /**
     * a synchronized (thread-safe) list of accounts
     */
    private static final List<Account> accountList = Collections.synchronizedList(new ArrayList<>());
    /**
     * a cryptographically strong random number generator used for generating the authentication tokens of the accounts
     */
    private static final SecureRandom secureRandom = new SecureRandom();
    /**
     * used for making the sequence of numbers for the messages sent, so that each message has a unique id
     */
    private static int nextMessageId = 1;

    private static void initialise() { //todo del
        accountList.add(new Account("user1", 1));
        accountList.add(new Account("user2", 2));
        accountList.add(new Account("user3", 3));

        ClientHandler clientHandler = new ClientHandler(new Socket());
        clientHandler.sendMessage("user1", "user2", "HELLO MY FRIEND");
        clientHandler.sendMessage("user1", "user2", "HEY ALL, SCOTT HERE");
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


    /**
     * This class handles the requests of the clients. It implements a number of specific functions:
     *     Create Account 1 <username>
     *     Show Accounts 2 <authToken>
     *     Send Message 3 <authToken> <recipient>
     *     Show Inbox 4 <authToken>
     *     ReadMessage 5 <authToken> <message_id>
     *     DeleteMessage 6 <authToken> <message_id>
     */
    private static class ClientHandler implements Runnable {
        /**
         * the client socket
         */
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

                System.out.print(response); //todo: del
                out.write(response);
//                String[] response_lines = response.split("\n");
//                for (String responseLine : response_lines) {
//                out.println(responseLine);
//                System.out.println(responseLine);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * Receives the request and depending on the function id (fn_id) returns the appropriate response string.
         * For all the functions, except the first one, an authToken validation process occurs to ensure that the user
         * making the request is indeed the owner of the account they want to access through the functions.
         * If the number of the parameters of the request is incorrect, then the response is "Wrong arguments".
         * @param request the request of the client as a string of the format "fn_id par1 par2 ..."
         * @return the appropriate response for each situation
         */
        private String handleRequest(String request) {
            String response = "Error";

            try {
                String[] parts = request.split(" ");
                int fn_id = Integer.parseInt(parts[0]);

                if (fn_id == 1) {
                    String username = parts[1];
                    response = createAccount(username);
                } else {
                    int authtoken = Integer.parseInt(parts[1]);
                    if (!authTokenExists(authtoken))
                        response = "Invalid Auth Token";

                    switch (fn_id) {
                        case 2:
                            response = showAccounts();
                            break;
                        case 3:
                            StringBuilder message = new StringBuilder();
                            for (int i = 3; i < parts.length; i++) {
                                message.append(parts[i]).append(" ");
                            }
                            if (! message.isEmpty())
                                message.deleteCharAt(message.length()-1);
                            String recipient = parts[2];
                            response = sendMessage(findAccountFromAuthtoken(authtoken).getUsername(), recipient, message.toString());
                            break;
                        case 4:
                            response = showInbox(authtoken); //todo make while loop
                            break;
                        case 5:
                            int message_id = Integer.parseInt(parts[2]);
                            response = readMessage(authtoken, message_id);
                            break;
                        case 6:
                            int id = Integer.parseInt(parts[2]);
                            response = deleteMessage(authtoken, id);
                            break;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e){
                response = "Wrong arguments";
            }
            return response;
        }

        /**
         * Checks whether there exists an account with the specified username.
         * @param username the username
         * @return true if the username belongs to a valid account, false otherwise
         */
        private boolean usernameExists(String username) {
            for (Account account: accountList) {
                if (account.getUsername().equals(username))
                    return true;
            }
            return false;
        }

        /**
         * Checks whether there exists an account with the specified authToken.
         * @param authToken the authToken
         * @return true if the authToken belongs to a valid account, false otherwise
         */
        private boolean authTokenExists(int authToken) {
            for (Account account: accountList) {
                if (account.getAuthToken() == authToken)
                    return true;
            }
            return false;
        }

        /**
         * Finds the account with the specified authToken
         * @param authToken the authToken
         * @return the requested account if the authToken is valid, null otherwise
         */
        private Account findAccountFromAuthtoken(int authToken) {
            for (Account account: accountList) {
                if (account.getAuthToken() == authToken)
                    return account;
            }
            return null;
        }

        /**
         * Finds the account with the specified username
         * @param username the username
         * @return the requested account if the username is valid, null otherwise
         */
        private Account findAccountFromUsername(String username) {
            for (Account account: accountList)
                if (account.getUsername().equals(username))
                    return account;
            return null;
        }

        /**
         * Implements the first function (create account).
         * @param username the username of the new account
         * @return the authToken of the new account if successful, otherwise an error message
         */
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

        /**
         * Implements the second function (show accounts).
         * @return a string containing a list of all the accounts on the server
         */
        private String showAccounts() {
            StringBuilder response = new StringBuilder();

            int i = 1;
            for (Account account: accountList) {
                response.append(i++).append(". ");
                response.append(account.getUsername());
                response.append("\n");
            }
            if (! response.isEmpty())
                response.deleteCharAt(response.length()-1);

            return response.toString();
        }

        /**
         * Implements the third function (send message).
         * @param sender the username of the sender
         * @param recipient the username of the recipient
         * @param message_body the text (body) of the message
         * @return "OK" if the message was sent successfully, "User does not exist" if the recipient does not exist
         */
        private synchronized String sendMessage(String sender, String recipient, String message_body) {
            if (!usernameExists(recipient))
                return "User does not exist";

            Message message = new Message(nextMessageId++, sender, recipient, message_body);
            Account recipientAccount = findAccountFromUsername(recipient);
            assert recipientAccount != null;
            recipientAccount.addToMessageBox(message);
            return "OK";
        }

        /**
         * Implements the fourth function (show inbox). Uses the getAllMessages method of the Account class.
         * @param authToken the authToken
         * @return a string with all the messages in the messageBox of the account
         */
        private String showInbox(int authToken) {
            Account account = findAccountFromAuthtoken(authToken);
            assert account != null;
            return account.getAllMessages();
        }

        /**
         * Implements the fifth function (read message).
         * @param authToken the authToken
         * @param message_id the id of the message
         * @return a string with the requested message in the format "(sender) message_body"
         *         or an error message if the message_id is invalid
         */
        private String readMessage(int authToken, int message_id) {
            String error = "Message ID does not exist";

            Account account = findAccountFromAuthtoken(authToken);
            if (account == null)
                return error;

            Message message = account.getMessageFromId(message_id);
            if (message == null)
                return error;

            message.setToRead();
            return "(" + message.getSender() + ") " + message.getBody();
        }

        /**
         * Implements the sixth function (delete message).
         * @param authToken the authToken
         * @param message_id the id of the message
         * @return "OK" if the deletion of the message is successful
         *         or an error message if the message_id is invalid
         */
        private String deleteMessage(int authToken, int message_id) {
            String error = "Message does not exist";

            Account account = findAccountFromAuthtoken(authToken);
            if (account == null)
                return error;

            if (account.deleteMessage(message_id))
                return "OK";
            return error;
        }
    }
}
