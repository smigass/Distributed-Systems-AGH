package ds.agh.chatapp.user.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Color;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.user.User;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.ObservableList;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPManager implements MessageManager{
    private DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private byte[] receiveBuffer = new byte[4096];
    private Thread listenerThread;
    private ObservableList<Message> messages;

    public UDPManager(InetAddress serverAddress, int serverPort, int localPort) throws IOException {
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
            Logger.log("Failed to send UDP message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMulticastMessage(String message, String username, String group) {
        try {
            Message messageObject = new Message(username, message);
            messageObject.setTimestamp(LocalTime.now());
            MessageUtils.sendUDPMessage(messageObject, InetAddress.getByName(group), 54321);
        } catch (IOException e) {
            Logger.log("Failed to send multicast message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void listenForMessages() {
        while (!listenerThread.isInterrupted()) {
            try {
                Logger.log("Waiting for UDP messages on port " + socket.getLocalPort() + "...");
                receiveBuffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                Message deserializedMessage = MessageSerializer.deserializeMessage(packet.getData());
                deserializedMessage.setColor(Color.CYAN);
                messages.add(deserializedMessage);
            } catch (Exception e) {
                Logger.logError("Error while receiving UDP message: " + e.getMessage());
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
