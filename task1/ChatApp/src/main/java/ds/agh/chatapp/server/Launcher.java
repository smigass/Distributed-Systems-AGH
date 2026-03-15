package ds.agh.chatapp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private final static Logger logger = LoggerFactory.getLogger(Launcher.class);
    static void main(String[] args) {
        int port = 12345;
        boolean isMulticastEnabled = false;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (args.length > 1) {
                    isMulticastEnabled = Boolean.parseBoolean(args[1]);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid port number provided, using default port 12345");
            }
        }
        Server server = new Server(port, isMulticastEnabled);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
