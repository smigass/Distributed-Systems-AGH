package ds.agh.chatapp.ui;

import ds.agh.chatapp.common.model.Color;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class MessageBox extends HBox {
    private final LocalTime time;
    private final String username;
    private final String message;
    private Color textColor = Color.WHITE;

    private final static int FONT_SIZE = 14;

        public MessageBox(LocalTime time, String username, String message) {
            this.time = time;
            this.username = username;
            this.message = message;
            initialize();
        }

        public MessageBox(LocalTime time, String username, String message, Color color) {
            this.time = time;
            this.username = username;
            this.message = message;
            this.textColor = color;
            initialize();
        }

        private void initialize() {
            ObservableList<Node> children = this.getChildren();
            Text timeText = new Text(parseTime(this.time));
            Text usernameText = new Text(this.username + ": ");
            Text messageText = new Text(this.message);

            TextFlow textFlow = new TextFlow(timeText, usernameText, messageText);

            timeText.setFont(Font.font("JetBrains Mono", FONT_SIZE));
            usernameText.setFont(Font.font("JetBrains Mono", FONT_SIZE));
            messageText.setFont(Font.font("JetBrains Mono", FONT_SIZE));

            timeText.setStyle("-fx-fill: " + textColor.toHex() + "; -fx-font-size: 16px");
            usernameText.setStyle("-fx-fill: #d6d6d6;-fx-font-size: 16px");
            messageText.setStyle("-fx-fill: #d6d6d6;-fx-font-size: 16px");

            this.setStyle("-fx-padding: 3px 5px ");

            children.addAll(textFlow);
        }

        private String parseTime(LocalTime time) {
            return "[" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ";
        }
}
