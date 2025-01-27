/**
 * This class represents a message sent from one user to another.
 */
public class Message {
    /**
     * the unique id of the message
     */
    int id;
    /**
     * true if the message has been read by the recipient, false otherwise
     */
    private boolean isRead;
    /**
     * the usernames of the sender and the receiver as well as the text (body) of the message
     */
    private final String sender, receiver, body;

    public Message(int id, String sender, String receiver, String body) {
        this.id = id;
        isRead = false;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }
    public int getId() {return id;}
    public String getSender() {
        return sender;
    }
    public String getBody() {
        return body;
    }
    public boolean getIsRead() {
        return isRead;
    }
    public void setToRead() {
        isRead = true;
    }
}
