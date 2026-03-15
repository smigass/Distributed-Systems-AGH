package ds.agh.chatapp.server.service;

import ds.agh.chatapp.common.MessageUtils;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.server.Connection;
import ds.agh.chatapp.utils.Logger;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeepAliveManager implements Runnable{
    private final List<Connection> connections;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public KeepAliveManager(List<Connection> connections, ObservableList<String> connectedClients) {
        this.connections = connections;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Connection connection : connections) {
                    executor.submit(() -> {
                        sendKeepAlive(connection);
                    });
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void sendKeepAlive(Connection connection) {
        try {
            connection.sendOverTCP(createKeepAliveMessage());
        } catch (IOException e) {
            Logger.log("User " + connection.getUsername() + " disconnected. Removing connection.", true);
            connection.shutdown();
            connections.remove(connection);
        }
    }

    private Message createKeepAliveMessage() {
        Message keepAliveMessage = new Message("SERVER", "KEEP_ALIVE");
        keepAliveMessage.setTimestamp(LocalTime.now());
        return keepAliveMessage;
    }


}
