import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Account {
    private final String username;
    private final int authToken;
    private List<Message> messageBox;

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = Collections.synchronizedList(new ArrayList<Message>());
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

    public boolean deleteMessage(int id) {
        Message message = getMessageFromId(id);
        if (message == null)
            return false;
        messageBox.remove(message);
        return true;
    }
    public String getAllMessages(){
        StringBuilder inbox = new StringBuilder();
        for (Message message : messageBox) {
            inbox.append(message.id).append(" from: ").append(message.getSender());
            if (message.getIsRead())
                inbox.append("*");
            inbox.append("\n");
        }
        if (!inbox.isEmpty())
            inbox.deleteCharAt(inbox.length()-1);
        return inbox.toString();
    }
    public void foo() {
        System.out.println(messageBox.get(0).getBody());
    }
}
