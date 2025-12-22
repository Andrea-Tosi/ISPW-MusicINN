package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalendarGUI implements Initializable {
    @FXML
    private Button previousMonthButton;

    @FXML
    private ComboBox<Month> monthComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private Button nextMonthButton;

    @FXML
    private GridPane calendarGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawCalendar(LocalDate.now().getYear(),  LocalDate.now().getMonth().getValue());
        setupMonthComboBox();
        setupYearComboBox();
    }

    private void updateButtonsState() {
        Month currentMonth = monthComboBox.getValue();
        Integer currentYear = yearComboBox.getValue();

        if (currentMonth == null || currentYear == null) return;

        // Trova il valore minimo e massimo disponibili nella ComboBox degli anni, assumendo che gli anni siano ordinati
        Integer firstYear = yearComboBox.getItems().getFirst();
        Integer lastYear = yearComboBox.getItems().getLast();

        // Disabilita previousMonthButton se siamo a Gennaio del primo anno disponibile
        previousMonthButton.setDisable(currentMonth == Month.JANUARY && currentYear.equals(firstYear));

        // Disabilita nextMonthButton se siamo a Dicembre dell'ultimo anno disponibile
        nextMonthButton.setDisable(currentMonth == Month.DECEMBER && currentYear.equals(lastYear));
    }

    private void setupMonthComboBox() {
        // Aggiunge tutti i mesi dell'enum Month alla ComboBox
        monthComboBox.getItems().addAll(Month.values());

        // Imposta il convertitore per visualizzare i nomi in italiano
        monthComboBox.setConverter(new StringConverter<Month>() { // CLASSE ANONIMA: Invece di creare una classe che implementi questa classe astratta, la crea senza nome concretizzandone i metodi che nella classe sono astratti
            @Override
            public String toString(Month month) {
                if (month == null) return "";
                // Restituisce il nome completo (es. "Gennaio") in italiano
                return month.getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            }

            @Override
            public Month fromString(String string) {
                // La ComboBox non è editabile
                return null;
            }
        });

        // Seleziona il mese corrente come predefinito
        monthComboBox.setValue(LocalDate.now().getMonth());

        // Listener che aggiorna il calendario ogni volta che viene modificato il mese selezionato
        monthComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateCalendar();
            }
        });
    }

    private void setupYearComboBox() {
        // Popola la ComboBox
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 5; i++) {
            yearComboBox.getItems().add(i);
        }

        // Imposta l'anno corrente come default
        yearComboBox.setValue(currentYear);

        // Listener che aggiorna il calendario ogni volta che viene modificato l'anno selezionato
        yearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateCalendar();
            }
        });
    }

    @FXML
    void handleNextMonth(ActionEvent event) {
        Month currentMonth = monthComboBox.getValue();
        Integer currentYear = yearComboBox.getValue();

        if (currentMonth == Month.DECEMBER) {
            monthComboBox.setValue(Month.JANUARY);
            yearComboBox.setValue(currentYear + 1);
        } else {
            // plus(1) restituisce automaticamente il mese successivo
            monthComboBox.setValue(currentMonth.plus(1));
        }
    }

    @FXML
    void handlePreviousMonth(ActionEvent event) {
        Month currentMonth = monthComboBox.getValue();
        Integer currentYear = yearComboBox.getValue();

        if (currentMonth == Month.JANUARY) {
            monthComboBox.setValue(Month.DECEMBER);
            yearComboBox.setValue(currentYear - 1);
        } else {
            // minus(1) restituisce il mese precedente
            monthComboBox.setValue(currentMonth.minus(1));
        }
    }

    private void updateCalendar() {
        int month = monthComboBox.getValue().getValue();
        int year = yearComboBox.getValue();

        drawCalendar(year, month);
    }

    private void drawCalendar(int year, int month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();

        int dayOfWeekOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        int currentDay = 1;

        setupRows(dayOfWeekOffset, daysInMonth);

        // Popolamento delle celle
        // Partiamo dalla riga 1 e proseguiamo finché ci sono giorni
        for (Node node : calendarGrid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);

            int row = (rowIndex == null) ? 0 : rowIndex;
            int col = (colIndex == null) ? 0 : colIndex;

            // Gestiamo solo i nodi dalla riga 1 in poi (i giorni)
            if (node instanceof VBox cellContainer) {
                if(row == 0 && col < dayOfWeekOffset) {
                    node.setVisible(false); // Basta disabilitare la visibilità per eliminare eventuali interazioni (click, etc.) con node
                } else if (currentDay > daysInMonth) {
                    node.setVisible(false);
                    node.setManaged(false);
                } else {
                    node.setVisible(true);
                    node.setManaged(true);

                    fillCell(cellContainer, currentDay);

                    currentDay++;
                }
            }
        }
        updateButtonsState();
    }

    public void setupRows(int dayOfWeekOffset, int daysInMonth) {
        // Somma l'offset del primo giorno ai giorni totali e dividiamo per 7 (colonne)
        int totalSlotsOccupied = dayOfWeekOffset + daysInMonth;
        int rowsNeeded = (int) Math.ceil(totalSlotsOccupied / 7.0);

        for (int i = 0; i <= 5; i++) {
            if (i <= rowsNeeded - 1) {
                // Imposta la percentuale equa per le righe attive
                calendarGrid.getRowConstraints().get(i).setPercentHeight(100.0 / rowsNeeded);
            } else {
                // Azzera la percentuale per le righe extra, facendole sparire
                calendarGrid.getRowConstraints().get(i).setPercentHeight(0);
            }
        }
    }

    private void fillCell(VBox cellContainer, int currentDay) {
        Label dayLabel = (Label) cellContainer.lookup("#dayLabel");
        if (dayLabel != null) {
            dayLabel.setText(String.valueOf(currentDay));
        }
        //TODO popolamento eventVBox
    }
}
//TODO stile CSS per dayLabel che indica giorno corrente
//TODO passaggio da mese n a mese n+1 quando si supera la mezzanotte con l'applicazione in esecuzione: in realtà la vista continua ad essere la stessa, cambia lo stile CSS sulla dayLabel che indica il giorno corrente
