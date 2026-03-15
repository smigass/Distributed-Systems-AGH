package ds.agh.chatapp.user;

import ds.agh.chatapp.common.model.Message;
import ds.agh.chatapp.ui.MessageBox;
import ds.agh.chatapp.ui.ProtocolChoiceBox;
import ds.agh.chatapp.common.model.ConnectionProtocol;
import ds.agh.chatapp.utils.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class UserPresenter {
    private User user;

    private UserService userService;

    private final ObservableList<ConnectionProtocol> connectionProtocols = FXCollections.observableArrayList(ConnectionProtocol.values());

    @FXML
    private Button nameButton;

    @FXML
    private Button leaveButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextField ServerIP;

    @FXML
    private HBox nameBox;

    @FXML
    private Label userName;

    @FXML
    private TextArea messageBox;

    @FXML
    private VBox messageBoxContainer;

    @FXML
    private Button sendButton;

    @FXML
    private VBox protocolBox;

    @FXML
    private CheckBox multicastCheck;

    @FXML
    public void initialize() {
        initListeners();
    }

    private void initUser(String username, String serverAddress){
        int port = 12345;
        List<String> splitAddress = List.of(serverAddress.split(":"));
        if (splitAddress.size() == 2 ){
            try {
                serverAddress = splitAddress.get(0);
                port = Integer.parseInt(splitAddress.get(1));
            } catch (NumberFormatException e) {
                Logger.logError("Invalid port number in server address: " + splitAddress.get(1) + ". Using default port " + port);
            }
        }
        user = new User(username, multicastCheck.selectedProperty().get());
        userName.setText("Logged in as: " + username);
        userName.setStyle("-fx-fill: #b99d9d;");
        userService = new UserService(user);
        if (userService.connectToServer(serverAddress, port) == 1) {
            userName.setText("Failed to connect.");
            return;
        }
        nameBox.setVisible(false);

        userService.getMessages().addListener(((ListChangeListener<Message>)change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Message msg : change.getAddedSubList()) {
                        MessageBox messageBox = new MessageBox(msg.getTimestamp(), msg.getUserName(), msg.getContent(), msg.getColor());
                        Platform.runLater(() -> messageBoxContainer.getChildren().add(messageBox));
                    }
                }
            }
        }));

        ProtocolChoiceBox protocolChoiceBox = new ProtocolChoiceBox();
        protocolBox.getChildren().add(protocolChoiceBox);

        protocolChoiceBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            ConnectionProtocol selectedProtocol = connectionProtocols.get(newVal.intValue());
            userService.setSelectedProtocol(selectedProtocol);
        });
    }


    private void initListeners() {
        leaveButton.setOnAction(e -> shutdown());

        nameButton.setOnAction(e -> {
            String username = nameText.getText();
            if (username != null && !username.trim().isEmpty()) {
                initUser(username, ServerIP.getText());
            }
        });

        sendButton.setOnAction(e -> {
            if (user != null) {
                String content = messageBox.getText();
                if (content != null && !content.trim().isEmpty()) {
                    userService.sendMessage(content);
                    messageBox.clear();
                }
            } else {
                Logger.logError("User not initialized. Cannot send message.");
            }
        });
    }

    public void shutdown() {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        if (user != null) {
            userService.disconnect();
        }
        stage.close();
    }

}
