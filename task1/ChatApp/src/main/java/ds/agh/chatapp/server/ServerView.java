package ds.agh.chatapp.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerView extends Application {
    private ServerPresenter presenter;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server-view.fxml"));
        Parent root = fxmlLoader.load();
        presenter = fxmlLoader.getController();
        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Server logs");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        presenter.stop();
    }
}
