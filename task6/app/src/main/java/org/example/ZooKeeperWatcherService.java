package org.example;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;

public final class ZooKeeperWatcherService implements Watcher, AutoCloseable {

    public static final String NODE_A = "/a";
    private static final int SESSION_TIMEOUT_MS = 10_000;

    private final String connectString;
    private final ExternalProcessManager processManager;
    private final MainWindow window;

    private ZooKeeper zooKeeper;
    private final CountDownLatch connectedLatch = new CountDownLatch(1);
    private volatile boolean closed;
    private int lastChildCount = -1;

    public ZooKeeperWatcherService(
            String connectString,
            ExternalProcessManager processManager,
            MainWindow window) {
        this.connectString = connectString;
        this.processManager = processManager;
        this.window = window;
    }

    public void start() throws IOException, InterruptedException, KeeperException {
        window.setConnected(connectString);
        window.appendLog("Connecting to ZooKeeper cluster: " + connectString);

        zooKeeper = new ZooKeeper(connectString, SESSION_TIMEOUT_MS, this);
        connectedLatch.await();

        window.appendLog("Connected. Registering watches for /a.");
        refreshNodeAState();
    }

    public String buildTreeForNodeA() throws KeeperException, InterruptedException {
        ensureConnected();
        if (zooKeeper.exists(NODE_A, false) == null) {
            return "/a (node does not exist)";
        }
        return NODE_A + System.lineSeparator() + ZooTreeBuilder.buildTree(zooKeeper, NODE_A);
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected
                && event.getType() == Event.EventType.None) {
            connectedLatch.countDown();
            return;
        }

        if (closed || zooKeeper == null) {
            return;
        }

        if (event.getState() == Event.KeeperState.Expired) {
            window.appendLog("ZooKeeper session expired.");
            return;
        }

        if (event.getPath() == null) {
            return;
        }

        if (!NODE_A.equals(event.getPath())) {
            return;
        }

        window.appendLog("Watch event: " + event.getType() + " on " + event.getPath());

        try {
            switch (event.getType()) {
                case NodeCreated -> handleNodeCreated();
                case NodeDeleted -> handleNodeDeleted();
                case NodeChildrenChanged -> handleChildrenChanged();
                default -> refreshNodeAState();
            }
        } catch (Exception exception) {
            window.appendLog("Error handling event: " + exception.getMessage());
        }
    }

    private void refreshNodeAState() throws KeeperException, InterruptedException, IOException {
        if (zooKeeper.exists(NODE_A, this) != null) {
            handleNodeCreated();
        } else {
            window.setNodeExists(false);
            window.setChildrenCount(0);
            lastChildCount = -1;
        }
    }

    private void handleNodeCreated() throws IOException, KeeperException, InterruptedException {
        window.setNodeExists(true);
        window.appendLog("Node /a detected — starting external application.");

        processManager.start();
        window.setExternalProcessRunning(true, processManager.commandDescription());

        zooKeeper.exists(NODE_A, this);
        registerChildrenWatch();
    }

    private void handleNodeDeleted() {
        window.setNodeExists(false);
        window.appendLog("Node /a deleted — stopping external application.");

        processManager.stop();
        window.setExternalProcessRunning(false, processManager.commandDescription());
        window.setChildrenCount(0);
        lastChildCount = -1;

        try {
            zooKeeper.exists(NODE_A, this);
        } catch (KeeperException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            window.appendLog("Failed to re-register watch for /a creation: " + exception.getMessage());
        }
    }

    private void registerChildrenWatch() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(NODE_A, this);
        int currentCount = children.size();

        window.setChildrenCount(currentCount);
        if (lastChildCount >= 0 && currentCount > lastChildCount) {
            window.notifyChildrenAdded(lastChildCount, currentCount);
        }
        lastChildCount = currentCount;
    }

    private void handleChildrenChanged() throws KeeperException, InterruptedException {
        registerChildrenWatch();
    }

    private void ensureConnected() {
        if (zooKeeper == null) {
            throw new IllegalStateException("Not connected to ZooKeeper.");
        }
    }

    @Override
    public void close() {
        closed = true;
        processManager.stop();
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
