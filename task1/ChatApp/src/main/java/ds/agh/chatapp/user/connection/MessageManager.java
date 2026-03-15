package ds.agh.chatapp.user.connection;

public interface MessageManager {
        void sendMessage(String message, String username);
        void listenForMessages() ;
}
