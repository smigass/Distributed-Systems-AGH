package org.example;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class MainWindow extends JFrame {

    private final JLabel connectionLabel = new JLabel("Connection: connecting...");
    private final JLabel nodeLabel = new JLabel("Node /a: does not exist");
    private final JLabel processLabel = new JLabel("External application: stopped");
    private final JLabel childrenLabel = new JLabel("Children of /a: -");
    private final JTextArea logArea = new JTextArea(8, 60);
    private final Supplier<String> treeSupplier;

    public MainWindow(Supplier<String> treeSupplier) {
        super("ZooKeeper Watcher - node /a");
        this.treeSupplier = treeSupplier;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel statusPanel = new JPanel(new BorderLayout(4, 4));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel labels = new JPanel(new java.awt.GridLayout(4, 1, 4, 4));
        labels.add(connectionLabel);
        labels.add(nodeLabel);
        labels.add(processLabel);
        labels.add(childrenLabel);
        statusPanel.add(labels, BorderLayout.CENTER);

        JButton showTreeButton = new JButton("Show /a tree structure");
        showTreeButton.addActionListener(event -> showTreeDialog());
        statusPanel.add(showTreeButton, BorderLayout.SOUTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        add(statusPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void setConnected(String connectString) {
        runOnEdt(() -> connectionLabel.setText("Connection: " + connectString));
    }

    public void setNodeExists(boolean exists) {
        runOnEdt(() -> nodeLabel.setText("Node /a: " + (exists ? "exists" : "does not exist")));
    }

    public void setExternalProcessRunning(boolean running, String command) {
        runOnEdt(() -> processLabel.setText(
                "External application: " + (running ? "running (" + command + ")" : "stopped")));
    }

    public void setChildrenCount(int count) {
        runOnEdt(() -> childrenLabel.setText("Children of /a: " + count));
    }

    public void notifyChildrenAdded(int previousCount, int currentCount) {
        runOnEdt(() -> {
            setChildrenCount(currentCount);
            JOptionPane.showMessageDialog(
                    this,
                    "A child was added to /a.\nCurrent child count: " + currentCount
                            + (previousCount >= 0 ? "\nPrevious count: " + previousCount : ""),
                    "Children changed on node /a",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void appendLog(String message) {
        runOnEdt(() -> {
            logArea.append(message + System.lineSeparator());
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void showTreeDialog() {
        runOnEdt(() -> {
            try {
                String tree = treeSupplier.get();
                JTextArea treeArea = new JTextArea(tree, 24, 48);
                treeArea.setEditable(false);
                treeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                JOptionPane.showMessageDialog(
                        this,
                        new JScrollPane(treeArea),
                        "Tree structure of /a",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to read tree /a: " + exception.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void runOnEdt(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
