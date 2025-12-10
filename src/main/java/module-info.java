module org.musicinn.musicinn {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.musicinn.musicinn to javafx.fxml;
    opens org.musicinn.musicinn.controller.controllerGUI to javafx.fxml;
    exports org.musicinn.musicinn;
}