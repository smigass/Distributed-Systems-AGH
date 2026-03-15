package ds.agh.chatapp.user.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Color;
import ds.agh.chatapp.common.model.Message;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;

public class UDPManager implements MessageManager{
    private final Logger logger = LoggerFactory.getLogger(UDPManager.class);
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private final Thread listenerThread;
    private ObservableList<Message> messages;

    public UDPManager(InetAddress serverAddress, int serverPort, int localPort, ObservableList<Message> messages) throws IOException {
        this.messages = messages;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        socket = new DatagramSocket(localPort);
        this.listenerThread = Thread.startVirtualThread(this::listenForMessages);
    }

    @Override
    public void sendMessage(String message, String username) {
        try {
            Message messageObject = new Message(username, message);
            messageObject.setTimestamp(LocalTime.now());
            MessageUtils.sendUDPMessage(messageObject, serverAddress, serverPort);
        } catch (IOException e) {
            logger.info("Failed to send UDP message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMulticastMessage(String message, String username, String group) {
        try {
            Message messageObject = new Message(username, message);
            messageObject.setTimestamp(LocalTime.now());
            MessageUtils.sendUDPMessage(messageObject, InetAddress.getByName(group), 54321);
        } catch (IOException e) {
            logger.error("Failed to send multicast message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void listenForMessages() {
        while (!listenerThread.isInterrupted()) {
            try {
                logger.info("Waiting for UDP messages on port {}...", socket.getLocalPort());
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                Message deserializedMessage = MessageSerializer.deserializeMessage(packet.getData());
                logger.info("Received UDP message: {} from {}:{}", deserializedMessage, packet.getAddress(), packet.getPort());
                deserializedMessage.setColor(Color.CYAN);
                messages.add(deserializedMessage);
            } catch (Exception e) {
                logger.error("Error while receiving UDP message: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        this.listenerThread.interrupt();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public void setMessages(ObservableList<Message> messages) {
        this.messages = messages;
    }
}
