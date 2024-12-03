public class Message {
    private boolean isRead;
    private final String sender, receiver, body;

    public Message(String sender, String receiver, String body) {
        isRead = false;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }
    public String getBody() {
        return body;
    }
}
