package ds.agh.chatapp.user;

import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.user.connection.TCPManager;
import ds.agh.chatapp.user.connection.UDPManager;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.InetAddress;

public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
