package org.musicinn.musicinn.controller.controller_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class HomepageManagerControllerGUI {
    @FXML
    private ImageView iconUser; //TODO verifica URL Spoti e Insta, modifica rider tecnico, logout, forse modifica profilo

    @FXML
    private Button publishAnnouncementButton;

    @FXML
    private Button acceptApplicationButton;

    @FXML
    private Button managePaymentsButton;

    @FXML
    private Button reviewButton;

    //    @FXML
//    private Node calendar; //serve per modificare l'aspetto grafico dell'elemento, ma potrebbe non servire nel mio caso
    //ad esempio se introducessi la possibilità di scegliere se visualizzare le date fissate in un calendario o in un carosello
    @FXML
    private CalendarGUI calendarGUI;
}
