package org.musicinn.musicinn.controller.controller_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.musicinn.musicinn.controller.controller_application.CalendarController;
import org.musicinn.musicinn.util.FxmlPathLoader;
import org.musicinn.musicinn.util.bean.SchedulableEventBean;
import org.musicinn.musicinn.util.exceptions.PersistenceException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalendarControllerGUI implements Initializable {
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

    private final List<CalendarCellControllerGUI> cellControllersGUI = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMonthComboBox();
        setupYearComboBox();
        setupCalendarStructure();
        drawCalendar(LocalDate.now().getYear(),  LocalDate.now().getMonth().getValue());
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

    private void setupCalendarStructure() {
        for (int currentCell = 0; currentCell < 42; currentCell++) {
            try {
                String fxmlPath = FxmlPathLoader.getPath("fxml.calendar.cell");
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                VBox cellNode = loader.load();
                CalendarCellControllerGUI cellControllerGUI = loader.getController();

                cellControllersGUI.add(cellControllerGUI);
                calendarGrid.add(cellNode, currentCell % 7, currentCell / 7);

                // Impostiamo constraints per il ridimensionamento
                GridPane.setHgrow(cellNode, Priority.ALWAYS);
                GridPane.setVgrow(cellNode, Priority.ALWAYS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        for (int currentCell = 0; currentCell < 42; currentCell++) { // 6 righe * 7 colonne
            CalendarCellControllerGUI cellControllerGUI = cellControllersGUI.get(currentCell);
            cellControllerGUI.resetCell();

            int dayToDisplay = currentCell - dayOfWeekOffset + 1;

            if (dayToDisplay > 0 && dayToDisplay <= daysInMonth) {
                LocalDate currentDate = LocalDate.of(year, month, dayToDisplay);
                cellControllerGUI.setDay(dayToDisplay);

                fillCell(cellControllerGUI, currentDate);
                // Logica per evidenziare oggi
                if (LocalDate.now().equals(currentDate)) {
                    cellControllerGUI.setAsToday();
                }
            }
        }
        updateButtonsState();
    }

    private void fillCell(CalendarCellControllerGUI cellControllerGUI, LocalDate date) {
        try {
            CalendarController controller = new CalendarController();
            // Recupera gli eventi per questa specifica data
            List<SchedulableEventBean> events = controller.getEventsForDate(date);

            for (SchedulableEventBean event : events) {
                cellControllerGUI.addEvent(event);
            }
        } catch (PersistenceException _) {
            System.err.println("Errore nell'interazione con il database per prelevare gli eventi");
        }
    }
}
