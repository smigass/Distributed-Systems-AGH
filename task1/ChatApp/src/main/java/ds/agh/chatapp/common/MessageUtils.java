package ds.agh.chatapp.common;

import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.utils.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageUtils {
    public static Message receiveAndDeserializeMessage(DataInputStream in) throws IOException {
        try {
            int messageLength = in.readInt();
            byte[] message = new byte[messageLength];
            in.readFully(message);

            return MessageSerializer.deserializeMessage(message);
        } catch (ClassNotFoundException e) {
            Logger.logError("Deserialization failed, no class found: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void sendUDPMessage(Message message, InetAddress address, int port) throws IOException {
        DatagramSocket udpSocket = new DatagramSocket();
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);
        if (serializedMessage.length > 1024) {
            Logger.logError("Message is too large to send via UDP: " + message);
            return;
        }
        DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, address, port);
        udpSocket.send(packet);
    }
}
