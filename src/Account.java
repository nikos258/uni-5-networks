import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents an account of a user
 */
public class Account {
    private final String username;
    /**
     * the unique, secret identification number of the account
     */
    private final int authToken;
    /**
     * a list of the messages the account has received
     */
    private final List<Message> messageBox;

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = Collections.synchronizedList(new ArrayList<Message>()); // a synchronized (thread-safe) list of messages
    }

    public String getUsername() {
        return username;
    }
    public int getAuthToken() {
        return authToken;
    }
    public void addToMessageBox(Message message) {
        messageBox.add(message);
    }

    /**
     * Returns the message that corresponds to the given id from the messageBox of the account.
     * @param id the unique id of the message
     * @return the message that corresponds to the given id or null if the id is invalid
     */
    public Message getMessageFromId(int id) {
        synchronized (messageBox) {
            Message message = null;

            Iterator<Message> it = messageBox.iterator();
            while (it.hasNext()){
                message = it.next();
                if (id == message.getId()) {
                    return message;
                }
            }
        }
        return null;
    }

    /**
     * Deletes the message that corresponds to the given id from the messageBox of the account.
     * @param id the unique id of the message
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteMessage(int id) {
        Message message = getMessageFromId(id);

        synchronized (messageBox) {
            if (message == null)
                return false;
            messageBox.remove(message);
        }
        return true;
    }

    /**
     * Makes a string containing a list of all the messages of the account. The format of each entry on the list is:
     * "id. from: sender*", where the asterisk only appears if the message has not been read.
     * @return a string with all the messages in the messageBox
     */
    public String getAllMessages(){
        StringBuilder inbox = new StringBuilder();
        for (Message message : messageBox) {
            inbox.append(message.id).append(". from: ").append(message.getSender());
            if (message.getIsRead())
                inbox.append("*");
            inbox.append("\n");
        }
        if (!inbox.isEmpty())
            inbox.deleteCharAt(inbox.length()-1);
        return inbox.toString();
    }
}
