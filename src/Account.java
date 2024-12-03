import java.util.ArrayList;
import java.util.List;

public class Account {
    private String username;
    private int authToken;
    private List<Message> messageBox;

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = new ArrayList<>();
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
    public void foo() {
        System.out.println(messageBox.get(0).getBody());
    }
}
