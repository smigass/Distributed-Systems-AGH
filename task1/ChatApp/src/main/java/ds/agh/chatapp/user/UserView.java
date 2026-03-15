package ds.agh.chatapp.user;

import ds.agh.chatapp.utils.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class UserView extends Application {
    private UserPresenter presenter;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();
        this.presenter = loader.getController();
        Scene scene = new javafx.scene.Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Chat App - User");
        stage.show();

        stage.setOnCloseRequest(event -> {
            Logger.log("Shutting down user session...");
            presenter.shutdown();
        });
    }
}
