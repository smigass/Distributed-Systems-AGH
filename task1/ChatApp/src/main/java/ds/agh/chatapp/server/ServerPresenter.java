package ds.agh.chatapp.server;


import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.ui.MessageBox;
import ds.agh.chatapp.user.UserView;
import ds.agh.chatapp.utils.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerPresenter {
    private Server server;

    @FXML
    private Button joinButton;

    @FXML
    private Button stateButton;

    @FXML
    private VBox logsContainer;

    @FXML
    private VBox connectedUsersContainer;

    @FXML
    public void initialize() {
        this.server = new Server();
        setButtonListener();
    }

    public void stop() {
        server.stop();
    }

    private void setButtonListener() {
        joinButton.visibleProperty().bind(server.runningProperty());
        joinButton.setOnAction(event -> {
            createUserSession();
        });
        stateButton.textProperty().bind(
                Bindings.when(server.runningProperty())
                        .then("Stop Server")
                        .otherwise("Start Server")
        );
        stateButton.styleProperty().bind(
                Bindings.when(server.runningProperty())
                        .then("-fx-background-color: red; -fx-text-fill: white;")
                        .otherwise("-fx-background-color: green; -fx-text-fill: white;")
        );

        stateButton.setOnAction(event -> {
            if (server.runningProperty().get()) {
                server.stop();
            } else {
                server.initialize();
            }
        });

        server.getConnectedClients().addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> {
                connectedUsersContainer.getChildren().clear();
                server.getConnectedClients().forEach(client -> {
                    TextField userField = new TextField(client);
                    userField.setEditable(false);
                    connectedUsersContainer.getChildren().add(userField);
                });
            });
        } );

        Logger.serverLogs.addListener((ListChangeListener<Message>) logs -> {
            while (logs.next()) {
                if (logs.wasAdded()) {
                    logs.getAddedSubList().forEach(log -> {
                        MessageBox messageBox = new MessageBox(log.getTimestamp(), log.getUserName(), log.getContent(), log.getColor());
                        Platform.runLater(() -> {
                            logsContainer.getChildren().add(messageBox);
                        });
                    });
                }
            }
        });

    }

    private void createUserSession() {
        Stage newUserStage = new Stage();
        UserView userView = new UserView();
        try {
            userView.start(newUserStage);
        } catch (Exception e) {
            Logger.logError("Failed to start user session: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
