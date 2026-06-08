package org.example;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // default L&F is fine
        }

        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            System.out.println(CommandLineOptions.usage());
            return;
        }

        CommandLineOptions options;
        try {
            options = CommandLineOptions.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.err.println();
            System.err.println(CommandLineOptions.usage());
            System.exit(1);
            return;
        }

        ExternalProcessManager processManager = new ExternalProcessManager(options.externalCommand());

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(() -> {
                ZooKeeperWatcherService service = serviceRef.get();
                if (service == null) {
                    throw new IllegalStateException("ZooKeeper service is not ready yet.");
                }
                try {
                    return service.buildTreeForNodeA();
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            });
            window.setVisible(true);

            Thread serviceThread = new Thread(() -> runService(options, processManager, window), "zk-watcher");
            serviceThread.setDaemon(false);
            serviceThread.start();
        });
    }

    private static final java.util.concurrent.atomic.AtomicReference<ZooKeeperWatcherService> serviceRef =
            new java.util.concurrent.atomic.AtomicReference<>();

    private static void runService(
            CommandLineOptions options,
            ExternalProcessManager processManager,
            MainWindow window) {
        try (ZooKeeperWatcherService service = new ZooKeeperWatcherService(
                options.connectString(), processManager, window)) {
            serviceRef.set(service);
            service.start();

            Runtime.getRuntime().addShutdownHook(new Thread(service::close, "zk-shutdown"));

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            window.appendLog("Critical error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    static final class CommandLineOptions {
        private final String connectString;
        private final List<String> externalCommand;

        private CommandLineOptions(String connectString, List<String> externalCommand) {
            this.connectString = connectString;
            this.externalCommand = externalCommand;
        }

        String connectString() {
            return connectString;
        }

        List<String> externalCommand() {
            return externalCommand;
        }

        static CommandLineOptions parse(String[] args) {
            String connectString = null;
            List<String> externalCommand = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                if ("--zk".equals(args[i]) || "-z".equals(args[i])) {
                    if (++i >= args.length) {
                        throw new IllegalArgumentException("Missing value for " + args[i - 1]);
                    }
                    connectString = args[i];
                    continue;
                }

                if ("--app".equals(args[i])) {
                    if (++i >= args.length) {
                        throw new IllegalArgumentException("Missing value for --app");
                    }
                    if (args[i].startsWith("\"") && args[i].endsWith("\"")) {
                        externalCommand = ExternalProcessManager.parseCommand(
                                args[i].substring(1, args[i].length() - 1));
                    } else {
                        while (i < args.length) {
                            externalCommand.add(args[i++]);
                        }
                    }
                    break;
                }

                throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }

            if (connectString == null || connectString.isBlank()) {
                throw new IllegalArgumentException("Required parameter: --zk <connectString>");
            }
            if (externalCommand.isEmpty()) {
                throw new IllegalArgumentException("Required parameter: --app <command>");
            }

            return new CommandLineOptions(connectString, List.copyOf(externalCommand));
        }

        static String usage() {
            return """
                    Usage:
                      ./gradlew run --args='--zk <host:port,...> --app <command>'

                    Examples (macOS):
                      ./gradlew run --args='--zk localhost:2181,localhost:2182,localhost:2183 --app open -a Calculator'
                      ./gradlew run --args='--zk localhost:2181 --app open -a TextEdit'

                    Example (Linux):
                      ./gradlew run --args='--zk localhost:2181,localhost:2182,localhost:2183 --app xcalc'

                    Features:
                      - watch on /a creation  -> start external application
                      - watch on /a deletion  -> stop external application
                      - watch on /a children  -> graphical child count notification
                      - window button         -> show /a tree structure
                    """;
        }
    }
}
