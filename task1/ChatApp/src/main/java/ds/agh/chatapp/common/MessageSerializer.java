package ds.agh.chatapp.common;

import ds.agh.chatapp.common.model.Message;

import java.io.*;

public class MessageSerializer {
    public static byte[] serializeMessage(Message message) throws IOException {
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       ObjectOutputStream oos = new ObjectOutputStream(bos);
       oos.writeObject(message);
       oos.flush();
       return bos.toByteArray();
    }

    public static Message deserializeMessage(byte[] messageBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(messageBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Message) ois.readObject();
    }

}
