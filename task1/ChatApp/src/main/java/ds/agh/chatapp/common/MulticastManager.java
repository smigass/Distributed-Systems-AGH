package ds.agh.chatapp.common;

import ds.agh.chatapp.common.model.Color;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastManager implements Runnable{
    private MulticastSocket socket;
    private final ObservableList<Message> messages;

    public MulticastManager(String group, ObservableList<Message> messages) {
        this.messages = messages;
        this.initialize(group);
    }

    private void initialize(String g) {
        try {
            InetAddress group = InetAddress.getByName(g);
            int port = 54321;
            this.socket = new MulticastSocket(port);
            this.socket.joinGroup(group);
        } catch (IOException e) {
            Logger.log("Failed to initialize MulticastSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Message message = MessageSerializer.deserializeMessage(packet.getData());
                message.setColor(Color.MAGENTA);
                messages.add(message);
            } catch (IOException | ClassNotFoundException e) {
                Logger.log("Error while receiving multicast message: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
