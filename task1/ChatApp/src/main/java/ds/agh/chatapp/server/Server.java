package ds.agh.chatapp.server;

import ds.agh.chatapp.server.connection.ConnectionManager;
import ds.agh.chatapp.server.service.KeepAliveManager;
import ds.agh.chatapp.server.service.MessagePopulator;
import ds.agh.chatapp.utils.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Server {
    private int port = 12345;
    private BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private Thread managerThread;
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
    private ConnectionManager connectionManager;
    private ObservableList<String> connectedClients = FXCollections.observableArrayList();
    private KeepAliveManager keepAliveManager;

    public Server() {
        keepAliveManager = new KeepAliveManager(connections, connectedClients);
    }

    public void start() {
        setIsRunning(true);
        Logger.log("Server initialized on port " + port, true);
    }

    public void initialize() {
        connectionManager = new ConnectionManager(port);
        connectionManager.setConnectedClients(connectedClients);
        connectionManager.setConnections(connections);
        connectionManager.setKeepAliveManager(keepAliveManager);
        connectionManager.setMessagePopulator(new MessagePopulator(connections));
        managerThread = new Thread(connectionManager);
        managerThread.start();
        start();
    }

    public void stop() {
        try {
            connectionManager.stop();
            setIsRunning(false);
            managerThread.interrupt();
            Logger.log("Server stopped.", true);
        } catch (IOException e) {
            Logger.logError("Failed to stop server: " + e.getMessage());
        }
    }

    public BooleanProperty runningProperty() {
        return isRunning;
    }

    private void setIsRunning(boolean value) {
        Platform.runLater(() -> {
            isRunning.set(value);
        });
    }

    public ObservableList<String> getConnectedClients() {
        return connectedClients;
    }
}
