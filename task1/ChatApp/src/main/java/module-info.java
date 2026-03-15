module ds.agh.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ds.agh.chatapp to javafx.fxml;
    opens ds.agh.chatapp.server to javafx.fxml;
    opens ds.agh.chatapp.user to javafx.fxml;


    exports ds.agh.chatapp;
    exports ds.agh.chatapp.server;
    exports ds.agh.chatapp.user;
    exports ds.agh.chatapp.common.model;
    exports ds.agh.chatapp.user.connection;
    opens ds.agh.chatapp.user.connection to javafx.fxml;
    opens ds.agh.chatapp.common.model to javafx.fxml;
    exports ds.agh.chatapp.server.connection;
    opens ds.agh.chatapp.server.connection to javafx.fxml;
}