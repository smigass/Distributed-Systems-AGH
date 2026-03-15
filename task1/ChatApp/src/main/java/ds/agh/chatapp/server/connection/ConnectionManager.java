package ds.agh.chatapp.server.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MulticastManager;
import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.server.Connection;
import ds.agh.chatapp.server.service.KeepAliveManager;
import ds.agh.chatapp.server.service.MessagePopulator;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ConnectionManager implements Runnable {
    private final int port;
    private ServerSocket tcpServerSocket;
    private DatagramSocket udpServerSocket;
    private MessagePopulator messagePopulator;
    private final ObservableList<Message> messageBuffer = FXCollections.observableArrayList();
    private ObservableList<String> connectedClients;
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

    @Override
    public void run() {
        try {
            initializeSockets();
            initializeMessageBufferListener();
            tcpListenerThread = Thread.startVirtualThread(this::listenForTCPConnections);
            udpListenerThread = Thread.startVirtualThread(this::listenForUDPMessages);
            keepAliveThread = Thread.startVirtualThread(keepAliveManager);
            multicastManager = new MulticastManager("239.0.0.0", messageBuffer);
            multicastThread = Thread.startVirtualThread(multicastManager);

        } catch (IOException e) {
            Logger.logError("Failed to start ConnectionManager: " + e.getMessage());
            e.printStackTrace();
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
                Logger.logError("Error while receiving UDP message: " + e.getMessage(), true);
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            } catch (ClassNotFoundException e) {
                Logger.logError("Deserialization failed, no class found: " + e.getMessage(), true);
            }
        }
    }

    private void listenForTCPConnections(){
        try {
            Logger.log("Waiting for TCP connections on port " + port + "...", true);
            while (!tcpServerSocket.isClosed())  {
                Socket clientSocket = tcpServerSocket.accept();
                Connection connection = new Connection(clientSocket, messageBuffer);
                connection.setConnectedClients(connectedClients);
                connections.add(connection);
                Logger.log("New TCP connection from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort(), true);
            }
        } catch (IOException e) {
            Logger.logError("Error while accepting TCP connection: " + e.getMessage(), true);
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

    public void stop() throws IOException {
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
    }

    public void setConnectedClients(ObservableList<String> connectedClients) {
        this.connectedClients = connectedClients;
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
