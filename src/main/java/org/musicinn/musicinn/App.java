package org.musicinn.musicinn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("1.0.0_LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MusicINN");
        stage.setScene(scene);
        stage.show();
    }
}
