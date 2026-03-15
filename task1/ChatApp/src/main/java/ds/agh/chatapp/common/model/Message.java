package ds.agh.chatapp.common.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private String userName;
    private String content;
    private LocalTime timestamp;
    private Color color = Color.WHITE;

    public Message(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public Message(String userName, String content, Color color) {
        this.userName = userName;
        this.content = content;
        this.color = color;
    }


    @Override
    public String toString() {
        String timeString = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "[" + timeString + "] " + userName + ": " + content;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }
}
