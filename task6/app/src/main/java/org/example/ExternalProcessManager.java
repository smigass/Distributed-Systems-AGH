package org.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ExternalProcessManager {

    private final List<String> command;
    private final boolean macOs;
    private Process process;
    private String macOsAppName;
    private boolean active;

    public ExternalProcessManager(List<String> command) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("External application command cannot be empty.");
        }
        this.command = List.copyOf(command);
        this.macOs = System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    public synchronized boolean isRunning() {
        return active;
    }

    public synchronized void start() throws IOException {
        if (active) {
            return;
        }

        macOsAppName = detectMacOsAppName(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.inheritIO();
        process = builder.start();
        active = true;
    }

    public synchronized void stop() {
        if (!active) {
            return;
        }

        if (process != null) {
            if (process.isAlive()) {
                process.destroy();
                try {
                    if (!process.waitFor(3, TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                        process.waitFor(2, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    process.destroyForcibly();
                }
            }
            process = null;
        }

        if (macOsAppName != null) {
            quitMacOsApplication(macOsAppName);
            macOsAppName = null;
        }

        active = false;
    }

    public String commandDescription() {
        return String.join(" ", command);
    }

    public static List<String> parseCommand(String rawCommand) {
        if (rawCommand == null || rawCommand.isBlank()) {
            throw new IllegalArgumentException("External application command is missing.");
        }
        return Arrays.asList(rawCommand.trim().split("\\s+"));
    }

    private String detectMacOsAppName(List<String> command) {
        if (!macOs) {
            return null;
        }

        for (int i = 0; i < command.size() - 2; i++) {
            if ("open".equals(command.get(i)) && "-a".equals(command.get(i + 1))) {
                return stripQuotes(command.get(i + 2));
            }
        }
        return null;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private void quitMacOsApplication(String appName) {
        try {
            Process osascript = new ProcessBuilder(
                            "osascript", "-e", "tell application \"" + appName + "\" to quit")
                    .redirectErrorStream(true)
                    .start();
            osascript.waitFor(5, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            try {
                new ProcessBuilder("killall", appName).start().waitFor(3, TimeUnit.SECONDS);
            } catch (IOException | InterruptedException ignored) {
                if (exception instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
