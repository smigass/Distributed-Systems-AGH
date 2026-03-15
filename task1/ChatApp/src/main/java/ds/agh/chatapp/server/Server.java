package ds.agh.chatapp.server;

import ds.agh.chatapp.server.connection.ConnectionManager;
import ds.agh.chatapp.server.service.KeepAliveManager;
import ds.agh.chatapp.server.service.MessagePopulator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Server implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
    private final KeepAliveManager keepAliveManager = new KeepAliveManager(connections);
    private final boolean isMulticastEnabled;
    public Server(int port, boolean isMulticastEnabled) {
        this.port = port;
        this.isMulticastEnabled = isMulticastEnabled;
    }

    public void start() {
        logger.info("Server started on port {}", port);
    }

    @Override
    public void run() {
        ConnectionManager connectionManager = new ConnectionManager(port);
        connectionManager.setConnections(connections);
        connectionManager.setKeepAliveManager(keepAliveManager);
        connectionManager.setMessagePopulator(new MessagePopulator(connections));
        connectionManager.init(this.isMulticastEnabled);
        start();
    }
}
