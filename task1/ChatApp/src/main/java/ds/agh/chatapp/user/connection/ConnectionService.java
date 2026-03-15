package ds.agh.chatapp.user.connection;

import ds.agh.chatapp.common.MulticastManager;
import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.user.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectionService {
    private final Logger logger = LoggerFactory.getLogger(ConnectionService.class);
    private final SimpleObjectProperty<ConnectionProtocol> selectedProtocol;
    private TCPManager tcpManager;
    private UDPManager udpManager;
    private MulticastManager multicastManager;
    private ObservableList<Message> messages;
    private final boolean multicastMembership;

    private Thread multicastThread;

    public ConnectionService(SimpleObjectProperty<ConnectionProtocol> sp, boolean multicastMembership) {
        this.multicastMembership = multicastMembership;
        this.selectedProtocol = sp;
    }

    public void connectToServer(InetAddress serverAddress, int serverPort, User user) throws IOException {
        this.tcpManager = new TCPManager(serverAddress, serverPort, messages);
        if (!tcpManager.getSocket().isConnected()) {
            logger.error("TCP connection failed. Cannot initialize UDP connection.");
            return;
        }
        this.udpManager = new UDPManager(serverAddress, serverPort, tcpManager.getSocket().getLocalPort(), messages);
        this.tcpManager.setMessages(messages);
        this.udpManager.setMessages(messages);
        this.tcpManager.sendMessage("INIT", user.getUsername());
        if (multicastMembership) {
            logger.info("User is in multicast group, initializing MulticastManager...");
            this.multicastManager = new MulticastManager("239.0.0.0", messages);
            this.multicastThread = Thread.startVirtualThread(multicastManager);
        }
    }

    public void sendMessage(String message, String username) {
        String command = message.split(" ")[0].toLowerCase();
        String content = message.substring(command.length()).trim();
        switch (command) {
            case "/u" -> {
                udpManager.sendMessage(content, username);
                return;
            }
            case "/t" -> {
                tcpManager.sendMessage(content, username);
                return;
            }
            case "/m" -> {
                udpManager.sendMulticastMessage(content, username, "239.0.0.0");
                return;
            }
        }
        switch (selectedProtocol.get()) {
            case TCP -> tcpManager.sendMessage(message, username);
            case UDP -> udpManager.sendMessage(message, username);
            case UDP_MULTICAST -> udpManager.sendMulticastMessage(message, username, "239.0.0.0");
        }
    }


    public void shutdown() {
        if (tcpManager != null) {
            tcpManager.shutdown();
        }
        if (udpManager != null) {
            udpManager.shutdown();
        }
        if (multicastManager != null) {
            multicastThread.interrupt();
        }
    }

    public void setMessages(ObservableList<Message> messages) {
        this.messages = messages;
    }
}
