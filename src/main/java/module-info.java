module org.musicinn.musicinn {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.musicinn.musicinn to javafx.fxml;
    opens org.musicinn.musicinn.controller.controller_gui to javafx.fxml;
    exports org.musicinn.musicinn;
}