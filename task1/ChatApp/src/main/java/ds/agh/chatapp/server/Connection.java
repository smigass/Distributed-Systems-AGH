package ds.agh.chatapp.server;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.List;

public class Connection {
    private final Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;
    private Thread listeningThread;
    private final List<Message> messageBuffer;
    private ObservableList<String> connectedClients;

    public Connection(Socket clientSocket, List<Message> messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.clientSocket = clientSocket;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            sendGreeting();
            listeningThread = Thread.startVirtualThread(this::listenForMessages);
        } catch (Exception e) {
            Logger.logError("Failed to initialize TCP connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void listenForMessages() {
        while (!clientSocket.isClosed()) {
            try {
                Message deserializedMessage = MessageUtils.receiveAndDeserializeMessage(in);
                if (deserializedMessage != null && !deserializedMessage.getContent().equals("ALIVE")) {
                    if (!deserializedMessage.getContent().equals("INIT")) {
                        messageBuffer.add(deserializedMessage);
                    }
                    this.username = deserializedMessage.getUserName();
                    if (!connectedClients.contains(username)) {
                        connectedClients.add(username);
                    }
                    Logger.log("Received message: " + deserializedMessage.getContent(), true);
                }
            } catch (Exception e) {
                Logger.logError("Error while reading from TCP connection, disconnecting client: " + e.getMessage());
                shutdown();
            }
        }
    }

    public void sendOverUDP(Message message) {
        try {
            MessageUtils.sendUDPMessage(message, clientSocket.getInetAddress(), clientSocket.getPort());
        } catch (IOException e) {
            Logger.logError("Error while sending message over UDP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendOverTCP(Message message) throws IOException {
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);
        out.writeInt(serializedMessage.length);
        out.write(serializedMessage);
        out.flush();
    }

    private void sendGreeting() throws IOException {
        Message greetingMessage = new Message("Server", "Welcome to the chat!");
        greetingMessage.setTimestamp(LocalTime.now());
        sendOverTCP(greetingMessage);
    }

    public String getUsername() {
        return username;
    }

    public void shutdown() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                listeningThread.interrupt();
                if (username != null) {
                    connectedClients.remove(username);
                }
            }
        } catch (IOException e) {
            Logger.logError("Failed to close TCP connection: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void setConnectedClients(ObservableList<String> connectedClients) {
        this.connectedClients = connectedClients;
    }
}
