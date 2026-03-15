package ds.agh.chatapp.utils;

import ds.agh.chatapp.common.model.Color;
import ds.agh.chatapp.common.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ObservableList<Message> serverLogs = FXCollections.observableArrayList();

    public static void log(String message) {
        System.out.println(parseCurrentTime() + ": " + message);
    }

    public static void log(String message, boolean serverLog) {
        if (serverLog) {
            Message log = new Message("Server", message);
            log.setTimestamp(LocalTime.now());
            serverLogs.add(log);
        }
    }

    public static void logError(String message) {
        System.err.println(message);
    }

    public static void logError(String message, boolean serverLog) {
        if (serverLog) {
            Message log = new Message("Server", message, Color.MAGENTA);
            log.setTimestamp(LocalTime.now());
            serverLogs.add(log);
        }
    }

    private static String parseCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        return "[" + currentTime.format(dateFormatter) + "]";
    }
}
