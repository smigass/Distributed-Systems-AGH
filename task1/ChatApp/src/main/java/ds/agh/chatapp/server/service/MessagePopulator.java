package ds.agh.chatapp.server.service;

import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.server.Connection;
import ds.agh.chatapp.utils.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagePopulator {
    private final List<Connection> connections;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MessagePopulator(List<Connection> connections) {
        this.connections = connections;
    }

    public void populateMessage(Message message, ConnectionProtocol protocol) {
        executor.submit(() -> {
            for (Connection connection : connections) {
                try {
                    switch (protocol) {
                        case UDP -> connection.sendOverUDP(message);
                        case TCP -> connection.sendOverTCP(message);
                    }
                } catch (IOException e) {
                    Logger.logError("Error while sending message to client: " + e.getMessage());
                    e.printStackTrace();
                }
            }}
        );
    }
}
