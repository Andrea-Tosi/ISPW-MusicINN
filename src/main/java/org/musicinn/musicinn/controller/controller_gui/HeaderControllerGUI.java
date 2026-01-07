package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.NavigationGUI;

public class HeaderControllerGUI {
    @FXML
    private Label pageLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private ImageView userIcon;

    @FXML
    private ContextMenu menu;

    public void setPageLabelText(String s) {
        pageLabel.setText(s);
    }

    public void setUsernameLabelText(String s) {
        usernameLabel.setText(s);
    }

    public void handleMenu() {
        menu.show(usernameLabel, Side.BOTTOM, 0, 0);
    }

    public void handleUserIcon() {
        usernameLabel.fireEvent(new MouseEvent(
                MouseEvent.MOUSE_CLICKED, // Tipo di evento
                0, 0,                     // Coordinate X, Y (rispetto al nodo)
                0, 0,                     // Coordinate X, Y (rispetto allo schermo)
                MouseButton.PRIMARY,      // Tasto del mouse (Sinistro)
                1,                        // Numero di clic
                false, false, false, false, // Tasti modificatori (Shift, Ctrl, Alt, Meta)
                true,                     // Pulsante primario premuto
                false, false,             // Pulsanti secondari/centrali
                true,                     // Sintetizzato
                false,                    // Popup trigger
                false,                    // Still since press
                null                      // Pick result
        ));
    }

    public void handleRiderManagement() {
        String nextFxmlPath = FxmlPathLoader.getPath("fxml.management_technical_rider.view");
        Scene currentScene = usernameLabel.getScene();
        Stage stage = (Stage) currentScene.getWindow();
        NavigationGUI.navigateToPath(stage, nextFxmlPath);
    }
}
