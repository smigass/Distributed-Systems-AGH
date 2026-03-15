package ds.agh.chatapp.server;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.List;

public class Connection {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private final Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;
    private Thread listeningThread;
    private final List<Message> messageBuffer;

    public Connection(Socket clientSocket, List<Message> messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.clientSocket = clientSocket;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            sendGreeting();
            listeningThread = Thread.startVirtualThread(this::listenForMessages);
        } catch (Exception e) {
            logger.error("Failed to initialize TCP connection: {}", e.getMessage());
            assert listeningThread != null;
            listeningThread.interrupt();
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
                }
            } catch (Exception e) {
                logger.error("Error while reading from TCP connection: {}", e.getMessage());
                shutdown();
            }
        }
    }

    public void sendOverUDP(Message message) {
        try {
            MessageUtils.sendUDPMessage(message, clientSocket.getInetAddress(), clientSocket.getPort());
        } catch (IOException e) {
            logger.error("Failed to send UDP message to {}: {}", username, e.getMessage());
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
            }
        } catch (IOException e) {
            logger.error("Failed to close TCP connection for {}: {}", username, e.getMessage());
        }
    }
}
