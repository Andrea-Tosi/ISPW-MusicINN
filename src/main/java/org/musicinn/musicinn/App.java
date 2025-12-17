package org.musicinn.musicinn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String loginFxmlPath = FxmlPathLoader.getPath("fxml.login.view");
        FXMLLoader loader = new FXMLLoader(App.class.getResource(loginFxmlPath));
        Scene scene = new Scene(loader.load());
        stage.setTitle("MusicINN");
        stage.setScene(scene);
        stage.show();
    }
}
