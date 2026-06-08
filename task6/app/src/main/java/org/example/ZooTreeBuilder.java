package org.example;

import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public final class ZooTreeBuilder {

    private ZooTreeBuilder() {
    }

    public static String buildTree(ZooKeeper zooKeeper, String rootPath) throws KeeperException, InterruptedException {
        StringBuilder builder = new StringBuilder();
        appendNode(zooKeeper, rootPath, "", true, builder);
        return builder.toString();
    }

    private static void appendNode(
            ZooKeeper zooKeeper,
            String path,
            String prefix,
            boolean last,
            StringBuilder builder) throws KeeperException, InterruptedException {
        builder.append(prefix)
                .append(last ? "└── " : "├── ")
                .append(path.substring(path.lastIndexOf('/') + 1))
                .append('\n');

        List<String> children = zooKeeper.getChildren(path, false);
        children.sort(String::compareTo);

        String childPrefix = prefix + (last ? "    " : "│   ");
        for (int i = 0; i < children.size(); i++) {
            boolean childLast = i == children.size() - 1;
            appendNode(zooKeeper, path + "/" + children.get(i), childPrefix, childLast, builder);
        }
    }
}
