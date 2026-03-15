package ds.agh.chatapp.server.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MulticastManager;
import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.server.Connection;
import ds.agh.chatapp.server.service.KeepAliveManager;
import ds.agh.chatapp.server.service.MessagePopulator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ConnectionManager {
    private final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private final int port;
    private ServerSocket tcpServerSocket;
    private DatagramSocket udpServerSocket;
    private MessagePopulator messagePopulator;
    private final ObservableList<Message> messageBuffer = FXCollections.observableArrayList();
    private KeepAliveManager keepAliveManager;
    private List<Connection> connections;
    private MulticastManager multicastManager;

    private Thread tcpListenerThread;
    private Thread udpListenerThread;
    private Thread keepAliveThread;
    private Thread multicastThread;

    public ConnectionManager(int port) {
        this.port = port;
    }

    public void init(boolean isMulticastEnabled) {
        try {
            initializeSockets();
            initializeMessageBufferListener();
            tcpListenerThread = new Thread(this::listenForTCPConnections);
            udpListenerThread = new Thread(this::listenForUDPMessages);
            keepAliveThread = new Thread(keepAliveManager);
            if (isMulticastEnabled) {
                multicastManager = new MulticastManager("239.0.0.0", messageBuffer);
            }
            multicastThread = new Thread(multicastManager);
            tcpListenerThread.start();
            udpListenerThread.start();
            keepAliveThread.start();
            multicastThread.start();
        } catch (IOException e) {
            logger.error("Failed to start ConnectionManager: " + e.getMessage());
            this.stop();
        }
    }

    private void listenForUDPMessages() {
        byte[] buffer = new byte[4096];
        while (!udpServerSocket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpServerSocket.receive(packet);
                Message deserializedMessage = MessageSerializer.deserializeMessage(packet.getData());
                messagePopulator.populateMessage(deserializedMessage, ConnectionProtocol.UDP);
            } catch (IOException e) {
                logger.error("Error while receiving UDP message: {}", e.getMessage());
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            } catch (ClassNotFoundException e) {
                logger.error("Deserialization failed, no class found: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void listenForTCPConnections(){
        try {
            while (!tcpServerSocket.isClosed())  {
                Socket clientSocket = tcpServerSocket.accept();
                Connection connection = new Connection(clientSocket, messageBuffer);
                connections.add(connection);
            }
        } catch (IOException e) {
            logger.error("Error while accepting TCP connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeSockets() throws IOException {
        tcpServerSocket = new ServerSocket(port);
        udpServerSocket = new DatagramSocket(port);
    }

    private void initializeMessageBufferListener() {
        messageBuffer.addListener((ListChangeListener<Message>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Message message : change.getAddedSubList()) {
                        messagePopulator.populateMessage(message, ConnectionProtocol.TCP);
                    }
                }
            }
        });
    }

    public void stop(){
        try {
            if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
                tcpServerSocket.close();
            }
            if (udpServerSocket != null && !udpServerSocket.isClosed()) {
                udpServerSocket.close();
            }
            if (tcpListenerThread != null) {
                tcpListenerThread.interrupt();
            }
            if (udpListenerThread != null) {
                udpListenerThread.interrupt();
            }
            if (keepAliveThread != null) {
                keepAliveThread.interrupt();
            }
            if (multicastThread != null) {
                multicastThread.interrupt();
            }
            for (Connection connection : connections) {
                connection.shutdown();
            }
        } catch (IOException e) {
            logger.error("Error while stopping ConnectionManager: {}", e.getMessage());
            e.printStackTrace();
        }

    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public void setKeepAliveManager(KeepAliveManager keepAliveManager) {
        this.keepAliveManager = keepAliveManager;
    }

    public void setMessagePopulator(MessagePopulator messagePopulator) {
        this.messagePopulator = messagePopulator;
    }
}
