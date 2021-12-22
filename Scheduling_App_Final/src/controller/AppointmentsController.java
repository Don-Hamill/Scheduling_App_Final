package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import main.Main;
import model.Appointment;
import model.Customer;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import util.DBConnector;

/**
 * The controller class for the appointments view.
 *
 */
public class AppointmentsController {

    private ObservableList<String> contacts = FXCollections.observableArrayList();
    private ObservableList<Integer> customerIds = FXCollections.observableArrayList();
    private ObservableList<Appointment> apptsToSet = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private boolean isAlpha;

    @FXML private TableView<Appointment> appointmentTable;

    @FXML private TableColumn<Appointment, Integer> apptIdCol;

    @FXML private TableColumn<Appointment, String> titleCol;

    @FXML private TableColumn<Appointment, String> descriptionCol;

    @FXML private TableColumn<Appointment, String> locationCol;

    @FXML private TableColumn<Appointment, String> contactCol;

    @FXML private TableColumn<Appointment, String> typeCol;

    @FXML private TableColumn<Appointment, String> startDateTimeCol;

    @FXML private TableColumn<Appointment, String> endDateTimeCol;

    @FXML private TableColumn<Appointment, Integer> customerIdCol;

    @FXML private TableColumn<Appointment, Integer> UserIdCol;

    @FXML private TextField UserIdField;

    @FXML private TextField idField;

    @FXML private TextField titleField;

    @FXML private TextField locationField;

    @FXML private TextField customerIdField;

    @FXML private TextField endTimeField;

    @FXML private TextField startTimeField;

    @FXML private TextField typeField;

    @FXML private TextArea descriptionArea;

    @FXML private DatePicker endDatePicker;

    @FXML private DatePicker startDatePicker;

    @FXML private ComboBox<String> contactComboBox;

    @FXML private ComboBox<String> contactScheduleComboBox;

    @FXML private ComboBox<Integer> customerScheduleComboBox;

    @FXML private RadioButton monthRadioBtn;

    @FXML private RadioButton weekRadioBtn;

    /**
     * A listener for when an item is selected in the table and populates the fields
     *
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException {
        System.out.println("Initialize appointments");

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("displayStartDateTime"));
        endDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("displayEndDateTime"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        UserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        appointmentTable.setItems(Appointment.getAllAppointments());

        DBConnector.executeQuery("SELECT Contact_Name FROM contacts");
        while (DBConnector.getResults().next()) {
            contacts.add(DBConnector.getResults().getString(1));
        }

        for (Customer c : Customer.getAllCustomers()) {
            customerIds.add(c.getId());
        }

        contactScheduleComboBox.setItems(contacts);
        contactComboBox.setItems(contacts);
        customerScheduleComboBox.setItems(customerIds);
        weekRadioBtn.setToggleGroup(toggleGroup);
        monthRadioBtn.setToggleGroup(toggleGroup);

        //Lambda expression for populating or clearing fields from appointment table selection
        appointmentTable.getSelectionModel().selectedItemProperty().addListener((newSelection) -> {
            if (newSelection != null && getSelection() != null) {
                populateFields(getSelection());
            } else {
                clearFields();
            }
        });
    }

    /**
     * Gets the selected item in the appointments table.
     */
    Appointment getSelection() {
        return appointmentTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Populates the fields for modifying an appointment.
     *
     * @param appointment the appointment with data to use.
     */
    void populateFields(Appointment appointment) {
        System.out.println("Selected appointment: " + appointment.getTitle());
        idField.setText(Integer.toString(appointment.getId()));
        titleField.setText(appointment.getTitle());
        descriptionArea.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        contactComboBox.setValue(appointment.getContact());
        typeField.setText(appointment.getType());
        startDatePicker.setValue(appointment.getDisplayStartDate());
        endDatePicker.setValue(appointment.getDisplayEndDate());
        startTimeField.setText(appointment.getDisplayStartTime());
        endTimeField.setText(appointment.getDisplayEndTime());
        customerIdField.setText(Integer.toString(appointment.getCustomerId()));
        UserIdField.setText(Integer.toString(appointment.getUserId()));
    }

    /**
     * Clears every field.
     */
    void clearFields() {
        if (getSelection() != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }
        idField.clear();
        titleField.clear();
        descriptionArea.clear();
        locationField.clear();
        contactComboBox.setValue(null);
        contactScheduleComboBox.setValue(null);
        customerScheduleComboBox.setValue(null);
        typeField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        customerIdField.clear();
        monthRadioBtn.setSelected(false);
        weekRadioBtn.setSelected(false);
        appointmentTable.setItems(Appointment.getAllAppointments());
    }

    /**
     * Sorts the table view of appointments by month.
     */
    @FXML void sortByMonthListener() {
        apptsToSet.clear();
        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getStartDateTime().getMonth() == curTime.getMonth()) {
                apptsToSet.add(a);
            }
        }
        appointmentTable.setItems(apptsToSet);
    }

    /**
     * Sorts the table view of appointments by week.
     */
    @FXML void sortByWeekListener() {
        apptsToSet.clear();
        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime sunday = curTime.minusDays(curTime.getDayOfWeek().getValue());

        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getStartDateTime().isAfter(sunday) && a.getStartDateTime().isBefore(sunday.plusDays(6))) {
                apptsToSet.add(a);
            }
        }
        appointmentTable.setItems(apptsToSet);
    }

    /**
     * Sorts the table view for a selected contact.
     */
    @FXML void contactScheduleListener() {
        if (contactScheduleComboBox.getSelectionModel().getSelectedItem() != null) {
            apptsToSet.clear();
            for (Appointment a : Appointment.getAllAppointments()) {

                if (customerScheduleComboBox.getSelectionModel().getSelectedItem() != null) {
                    if (a.getContact().equals(contactScheduleComboBox.getSelectionModel().getSelectedItem()) && a.getCustomerId() == customerScheduleComboBox.getSelectionModel().getSelectedItem()) {

                        apptsToSet.add(a);
                    }
                }
                else if (a.getContact().equals(contactScheduleComboBox.getSelectionModel().getSelectedItem())){
                    apptsToSet.add(a);
                }
                appointmentTable.setItems(apptsToSet);
            }
        }
    }

    /**
     * Sorts the table view for a selected customer.
     */
    @FXML void customerScheduleListener() {

        if (customerScheduleComboBox.getSelectionModel().getSelectedItem() != null) {
            apptsToSet.clear();
            for (Appointment a : Appointment.getAllAppointments()) {
                if (a.getContact().equals(contactScheduleComboBox.getSelectionModel().getSelectedItem())) {
                    if (a.getCustomerId() == customerScheduleComboBox.getSelectionModel().getSelectedItem()) {
                        apptsToSet.add(a);
                    }
                }
            }
            appointmentTable.setItems(apptsToSet);
        }
    }

    /**
     * Updates the selected appointment with the data from each field if
     * they are all valid.
     *
     * @throws SQLException
     */
    @FXML void updateListener(ActionEvent event) throws SQLException {
        System.out.println("Update appointment");

        if (getSelection() != null) {
            if (fieldsValid(false)) {
                changeAppointments(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select an appointment to update.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * Adds a new appointment with the data from each of the fields if they are
     * valid.
     *
     * @throws SQLException
     */
    @FXML void addListener(ActionEvent event) throws SQLException {
        System.out.println("Add appointment");

        if (idField.getText().isBlank()) {
            changeAppointments(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Could not add appointment. Appt. ID " + idField.getText() + " already exists.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * Deletes the selected appointment
     *
     * @throws SQLException
     */
    @FXML void deleteListener(ActionEvent event) throws SQLException {
        System.out.println("Delete appointment");
        if (getSelection() != null) {
            String id = Integer.toString(getSelection().getId());
            String type = getSelection().getType();
            Appointment.deleteAppointment(getSelection());
            appointmentTable.setItems(Appointment.getAllAppointments());
            clearFields();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment with ID " + id + " deleted: " + type);
            alert.setTitle("Appointment deleted.");
            Optional<ButtonType> result = alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select an appointment to delete.");
            alert.setTitle("Error.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * Changes to the main menu view.
     *
     * @throws IOException
     */
    @FXML void backListener() throws IOException {
        System.out.println("Main menu");
        Main.setRoot("menu");
    }

    /**
     * Changes to the reports view.
     *
     * @throws IOException
     */
    @FXML void reportListener() throws IOException {
        System.out.println("Generate report");
        Main.setRoot("reports");
    }

    /**
     * Clears the current selection in the table view and fields.
     */
    @FXML void clearSelectionListener() {
        clearSelection();
    }

    /**
     * Clears the selection in the table view and the fields.
     */
    void clearSelection() {
        System.out.println("Clear selection");
        clearFields();
        if (getSelection() != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }
        if (toggleGroup.getSelectedToggle() != null) {
            toggleGroup.getSelectedToggle().setSelected(false);
        }
    }

    /**
     * Changes the appointments in the database and locally.
     *
     * @param add indicates whether the method should add vs. update an appointment.
     * @throws SQLException
     */
    void changeAppointments(boolean add) throws SQLException {
        if (fieldsValid(add)) {
            int id;
            if (add) {
                id = getNewId();
            } else {
                id = getSelection().getId();
            }


            String title = titleField.getText();
            String desc = descriptionArea.getText();
            String location = locationField.getText();
            String contact = contactComboBox.getValue();
            String type = typeField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String startTimeStr = startTimeField.getText();
            String endTimeStr = endTimeField.getText();
            int customerId = Integer.parseInt(customerIdField.getText());
            int userId = DBConnector.getLoggedInUserId();

            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalTime startTime = LocalTime.parse(startTimeStr, parser);
            LocalTime endTime = LocalTime.parse(endTimeStr, parser);
            ZonedDateTime utcStartDateTime = startDate.atTime(startTime).atZone(Main.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime utcEndDateTime = endDate.atTime(endTime).atZone(Main.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));

            Appointment newAppointment = new Appointment(id, title, desc, location, contact, type, utcStartDateTime.toLocalDateTime(), utcEndDateTime.toLocalDateTime(), customerId, userId);

            if (add) {
                Appointment.addAppointment(newAppointment);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment added: " + newAppointment.getTitle());
                alert.setTitle("Appointment added.");
                Optional<ButtonType> result = alert.showAndWait();
            } else {
                Appointment.updateAppointment(getSelection(), newAppointment);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment updated: " + newAppointment.getTitle());
                alert.setTitle("Appointment updated.");
                Optional<ButtonType> result = alert.showAndWait();
            }

            appointmentTable.setItems(Appointment.getAllAppointments());
            clearFields();
        }
    }



    /**
     * Checks whether the values in each field are valid.
     *
     * @param add indicates whether an appointment is being added or updated.
     * @return whether each field has a valid value.
     * @throws SQLException
     */
    Boolean fieldsValid(boolean add) throws SQLException {
        boolean rVal = true;
        boolean endTimeValid = false;
        boolean startTimeValid = false;
        boolean startDateExists = false;
        boolean endDateExists = false;
        int blanks = 0;

        if (customerIdField.getText().isBlank()) {
            blanks++;
            rVal = false;
        } else if (UserIdField.getText().isBlank()) {
            blanks++;
            System.out.println("userID field");
            rVal = false;
        } else if (isAlpha(customerIdField.getText())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid customer ID; must be an integer.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
            rVal = false;
        } else {
            DBConnector.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_ID = '" + customerIdField.getText() + "'");
            if (!DBConnector.getResults().next()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer ID does not exist in the database.");
                alert.setTitle("Error");
                Optional<ButtonType> result = alert.showAndWait();
                rVal = false;
            }
        }

        if (endTimeField.getText().isBlank()) {
            blanks++;
            rVal = false;
        } else if (!timeValid(endDatePicker.getValue(), endTimeField.getText(), "end")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid End time: Must be formatted to HH:mm.");
            alert.setTitle("Error");
            rVal = false;
        } else {
            endTimeValid = true;
        }

        if (startTimeField.getText().isBlank()) {
            blanks++;
            rVal = false;
        } else if (!timeValid(startDatePicker.getValue(), startTimeField.getText(), "start")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid Start time: Must be formatted to HH:mm.");
            alert.setTitle("Error");
            rVal = false;
        } else {
            startTimeValid = true;
        }

        if (endDatePicker.getValue() == null) {
            blanks++;
            rVal = false;
        } else {
            endDateExists = true;
        }
        if (startDatePicker.getValue() == null) {
            blanks++;
            rVal = false;
        } else {
            startDateExists = true;
        }

        if (startTimeValid && endTimeValid && startDateExists && endDateExists) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalDateTime startDateTime = startDatePicker.getValue().atTime(LocalTime.parse(startTimeField.getText(), parser));
            LocalDateTime endDateTime = endDatePicker.getValue().atTime(LocalTime.parse(endTimeField.getText(), parser));

            if (startDateTime.compareTo(endDateTime) >= 0) {
                //error = "Start date must be before end date.";
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start date must be before end date.");
                alert.setTitle("Error");
                Optional<ButtonType> result = alert.showAndWait();
                rVal = false;
            } else {
                ZonedDateTime userStart = startDateTime.atZone(Main.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime userEnd = endDateTime.atZone(Main.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));

                if (add) {
                    DBConnector.executeQuery("SELECT Start, End FROM appointments");
                } else {
                    DBConnector.executeQuery("SELECT Start, End FROM appointments WHERE Appointment_ID != " + getSelection().getId());
                }

                while(DBConnector.getResults().next()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(Main.getZoneId().toString()));
                    ZonedDateTime dbStart = LocalDateTime.parse(DBConnector.getResults().getString(1), formatter).atZone(ZoneId.of("UTC"));
                    ZonedDateTime dbEnd = LocalDateTime.parse(DBConnector.getResults().getString(2), formatter).atZone(ZoneId.of("UTC"));

                    if (!userStart.isAfter(dbEnd) && !userEnd.isBefore(dbStart)){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Could not schedule appointment: Date overlaps another appointment.");
                        alert.setTitle("Error");
                        Optional<ButtonType> result = alert.showAndWait();
                        rVal = false;
                    }
                }
            }
        }

        if (typeField.getText().isBlank()) {
            blanks++;
            rVal = false;
        }
        if (contactComboBox.getValue() == null) {
            blanks++;
            rVal = false;
        }
        if (locationField.getText().isBlank()) {
            blanks++;
            rVal = false;
        }
        if (descriptionArea.getText().isBlank()) {
            blanks++;
            rVal = false;
        }
        if (titleField.getText().isBlank()) {
            blanks++;
            rVal = false;
        }

        if (blanks > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "All fields required; " + blanks + " fields blank.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
        }

        return rVal;
    }




    /**
     * Changes the appointments in the database and locally.
     *
     * @param date the date to check.
     * @param time the time to check.
     * @param endOrStart indicates if this is the appointment end or start.
     * @return whether the time is valid.
     */
    Boolean timeValid(LocalDate date, String time, String endOrStart) {
        /*if (!time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid " + endOrStart + " time: Must be formatted to HH:mm.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
            return false;
        } else if (date != null) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");

            LocalTime locTime = LocalTime.parse(time, parser);

            ZonedDateTime zonedLocDateTime = date.atTime(locTime).atZone(Main.getZoneId());
            ZonedDateTime estZonedDateTime = zonedLocDateTime.withZoneSameInstant(ZoneId.of("US/Eastern"));

            DayOfWeek day = estZonedDateTime.toLocalDateTime().getDayOfWeek();

            boolean tooEarly = estZonedDateTime.toLocalDateTime().isBefore(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(8, 00)));
            boolean tooLate = estZonedDateTime.toLocalDateTime().isAfter(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(22, 00)));
            boolean onWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;


            if (tooEarly || tooLate || onWeekend) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid appointment " + endOrStart + " date/time; Outside of EST business hours 8:00 - 22:00 weekdays. Date given: " + estZonedDateTime.toLocalDateTime().toString() + " EST");
                alert.setTitle("Error");
                Optional<ButtonType> result = alert.showAndWait();
                return false;
            } else {
                return false;
            }
        }
        return true;*/
        if (!time.matches("(([0-1][0-9]|[2][0-3])[:][0-5][0-9])|(([0-1][0-9]|[2][0-4])[:][0-5][0-9][:][0-5][0-9])")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid " + endOrStart + " time: Must be formatted to HH:mm.");
            alert.setTitle("Error");
            Optional<ButtonType> result = alert.showAndWait();
            return false;
        } else if (date != null){
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalTime locTime = LocalTime.parse(time, parser);
            ZonedDateTime zonedLocDateTime = date.atTime(locTime).atZone(Main.getZoneId());
            ZonedDateTime estZonedDateTime = zonedLocDateTime.withZoneSameInstant(ZoneId.of("US/Eastern"));

            DayOfWeek day = estZonedDateTime.toLocalDateTime().getDayOfWeek();

            boolean tooEarly = estZonedDateTime.toLocalDateTime().isBefore(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(8, 00)));
            boolean tooLate = estZonedDateTime.toLocalDateTime().isAfter(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(22, 00)));
            boolean onWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

            if (tooEarly || tooLate || onWeekend) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Invalid appointment " + endOrStart + " date/time; Outside of EST business hours 8:00 - 22:00 weekdays. Date given: " + estZonedDateTime.toLocalDateTime().toString() + " EST");
                alert.setTitle("Error");
                Optional<ButtonType> result = alert.showAndWait();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Returns a new appointment id.
     *
     * @return a new appointment id.
     */
    int getNewId() {
        int max = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getId() > max) {
                max = a.getId();
            }
        }
        return max + 1;
    }

    /**
     * LAMBDA EXPRESSION: loops through the characters in a string to check if
     * it is made up of digits.
     *
     * @param string string to check.
     * @return whether all chars are digits.
     */
    boolean isAlpha(String string) {
        isAlpha = true;
        string.chars().forEach(character -> {
            if (!Character.isAlphabetic(character)) {
                isAlpha = false;
            }
        });
        return isAlpha;
    }

}
