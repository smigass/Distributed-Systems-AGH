package ds.agh.chatapp.user.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Color;
import ds.agh.chatapp.common.model.Message;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Objects;


public class TCPManager implements MessageManager{
    private final Logger logger = LoggerFactory.getLogger(TCPManager.class);
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final Thread listenerThread;
    private ObservableList<Message> messages;

    public TCPManager(InetAddress serverAddress, int serverPort, ObservableList<Message> messages) throws IOException{
        this.messages = messages;
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(serverAddress, serverPort), 5000);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.listenerThread = Thread.startVirtualThread(this::listenForMessages);
    }


    @Override
    public void sendMessage(String message, String username) {
        Message messageObject = new Message(username, message);
        messageObject.setTimestamp(LocalTime.now());
        try {
            byte[] serializedMessage = MessageSerializer.serializeMessage(messageObject);
            out.writeInt(serializedMessage.length);
            out.write(serializedMessage);
            out.flush();
            logger.info("Sent TCP message: {}", message);
        } catch (IOException e){
            logger.error("Failed to serialize message: {}", message);
            e.printStackTrace();
        }
    }


    @Override
    public void listenForMessages(){
        while(!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            try {
                Message deserializedMessage = MessageUtils.receiveAndDeserializeMessage(in);
                if (Objects.requireNonNull(deserializedMessage).getUserName().equals("SERVER") && deserializedMessage.getContent().equals("KEEP_ALIVE")) {
                    sendMessage("ALIVE", "CLIENT_ACK");
                    continue;
                }
                if (!deserializedMessage.getUserName().equals("SERVER")) {
                    deserializedMessage.setColor(Color.WHITE);
                    messages.add(deserializedMessage);
                }
            } catch (IOException e) {
                logger.error("Error while reading from TCP connection: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void setMessages(ObservableList<Message> messages) {
        this.messages = messages;
    }

    public void shutdown() {
        try {
            this.listenerThread.interrupt();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close TCP connection: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
