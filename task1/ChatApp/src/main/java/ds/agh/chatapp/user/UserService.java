package ds.agh.chatapp.user;

import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.user.connection.ConnectionService;
import ds.agh.chatapp.utils.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.InetAddress;


public class UserService {
    private final User currentUser;
    private final SimpleObjectProperty<ConnectionProtocol> selectedProtocol = new SimpleObjectProperty<>(ConnectionProtocol.TCP);
    private final ConnectionService connectionService;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    public UserService(User user) {
        this.currentUser = user;
        this.connectionService = new ConnectionService(selectedProtocol, user.isInMulticastGroup());
        this.connectionService.setMessages(messages);
    }

    public int connectToServer(String serverIP, int serverPort) {
        try {
            connectionService.connectToServer(InetAddress.getByName(serverIP), serverPort, currentUser);
            return 0;
        } catch (Exception e) {
            Logger.logError("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    public void sendMessage(String message) {
        connectionService.sendMessage(message, currentUser.getUsername());
    }


    public void setSelectedProtocol(ConnectionProtocol selectedProtocol) {
        this.selectedProtocol.set(selectedProtocol);
        Logger.log("Selected protocol changed to: " + selectedProtocol);
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void disconnect() {
        connectionService.shutdown();
    }
}
