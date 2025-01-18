public class Message {
    int id;
    private boolean isRead;
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
}
