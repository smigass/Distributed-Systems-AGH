package ds.agh.chatapp.user.connection;

import ds.agh.chatapp.common.MessageSerializer;
import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.user.User;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPManager implements MessageManager{
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread listenerThread;
    private ObservableList<Message> messages;

    public TCPManager(InetAddress serverAddress, int serverPort, User user) throws IOException{
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
        } catch (IOException e){
            Logger.logError("Failed to serialize message: " + message);
            e.printStackTrace();
        }
    }


    @Override
    public void listenForMessages(){
        while(!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            try {
                Message deserializedMessage = MessageUtils.receiveAndDeserializeMessage(in);
                if (deserializedMessage.getUserName().equals("SERVER") && deserializedMessage.getContent().equals("KEEP_ALIVE")) {
                    sendMessage("ALIVE", "CLIENT_ACK");
                    continue;
                }
                if (!deserializedMessage.getUserName().equals("SERVER")) {
                    messages.add(deserializedMessage);
                }
            } catch (IOException e) {
                Logger.logError("Error while reading from TCP connection: " + e.getMessage());
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
            Logger.logError("Failed to close TCP connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
