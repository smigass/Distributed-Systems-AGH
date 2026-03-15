package ds.agh.chatapp.user;

public class User {
    private final String username;
    private final boolean inMulticastGroup;

    public User(String username, boolean inMulticastGroup) {
        this.username = username;
        this.inMulticastGroup = inMulticastGroup;
        System.out.println("User created: " + username + ", inMulticastGroup: " + inMulticastGroup);
    }

    public String getUsername() {
        return username;
    }

    public boolean isInMulticastGroup() {
        return inMulticastGroup;
    }
}
