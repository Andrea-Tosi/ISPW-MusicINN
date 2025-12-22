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

//    @FXML
//    private Button previousMonthButton;
//
//    @FXML
//    private ComboBox<Month> monthComboBox;
//
//    @FXML
//    private ComboBox<Integer> yearComboBox;
//
//    @FXML
//    private Button nextMonthButton;
//
//    @FXML
//    private GridPane calendarGrid;
//
//    // Per avere un unico array CalendarCell e non due array (uno Label e uno VBox)
//    private static class CalendarCell {
//        Label dayLabel;
//        VBox eventVBox;
//    }
//
//    private CalendarCell[][] cells = new CalendarCell[6][7];
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        mapComponentsGrid();
//        setupMonthComboBox();
//        setupYearComboBox();
//    }
//
//    private void setupMonthComboBox() {
//        // Aggiunge tutti i mesi dell'enum Month alla ComboBox
//        monthComboBox.getItems().addAll(Month.values());
//
//        // Imposta il convertitore per visualizzare i nomi in italiano
//        monthComboBox.setConverter(new StringConverter<Month>() { // CLASSE ANONIMA: Invece di creare una classe che implementi questa classe astratta, la crea senza nome concretizzandone i metodi che nella classe sono astratti
//            @Override
//            public String toString(Month month) {
//                if (month == null) return "";
//                // Restituisce il nome completo (es. "Gennaio") in italiano
//                return month.getDisplayName(TextStyle.FULL, Locale.ITALIAN);
//            }
//
//            @Override
//            public Month fromString(String string) {
//                // La ComboBox non è editabile
//                return null;
//            }
//        });
//
//        // Seleziona il mese corrente come predefinito
//        monthComboBox.setValue(LocalDate.now().getMonth());
//    }
//
//    private void setupYearComboBox() {
//        // Popola la ComboBox
//        int currentYear = LocalDate.now().getYear();
//        for (int i = currentYear; i <= currentYear + 5; i++) {
//            yearComboBox.getItems().add(i);
//        }
//
//        // Imposta l'anno corrente come default
//        yearComboBox.setValue(currentYear);
//
////        // 3. Gestisci il cambiamento di valore
////        yearComboBox.setOnAction(event -> {
////            Integer selectedYear = yearComboBox.getValue();
////            System.out.println("Anno selezionato: " + selectedYear);
////            // Qui chiamerai il tuo metodo per ridisegnare il calendario
////        });
//    }
//
//    // Popola l'array bidimensionale cells
//    private void mapComponentsGrid() {
//        // Cicla su tutte le possibili celle 7x7
//        for (Node node : calendarGrid.getChildren()) {
//            Integer col = GridPane.getColumnIndex(node);
//            Integer row = GridPane.getRowIndex(node);
//
//            if (node instanceof VBox cellContainer && row != null && col != null) {
//
//                CalendarCell cell = new CalendarCell();
//                // Cerca i componenti dentro il contenitore della cella tramite ID (anche se tutte le label e tutti i vbox hanno lo stesso ID, va bene lo stesso perché eseguo la ricerca solo all'interno della cella)
//                cell.dayLabel = (Label) cellContainer.lookup("#dayLabel");
//                cell.eventVBox = (VBox) cellContainer.lookup("#eventVBox");
//
//                cells[row][col] = cell;
//            }
//        }
//    }
}
