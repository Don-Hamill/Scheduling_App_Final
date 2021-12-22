package controller;

import main.Main;
import model.Appointment;
import java.io.IOException;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import util.DBConnector;

/**
 * The controller class for the reports view.
 */
public class ReportController {

    ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March",
            "April", "May", "June", "July", "August", "September", "October", "November", "December");
    int numAppointments = 0;

    @FXML private ComboBox<String> typeComboBox;

    @FXML private ComboBox<String> monthComboBox;

    @FXML private Label numAppointmentsLabel;

    /**
     * Populates the appointments list with updated data and sets the items in the months and type combo boxes.
     *
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException {
        DBConnector.populateAppts();

        ObservableList<String> types = FXCollections.observableArrayList();

        for (Appointment a : Appointment.getAllAppointments()) {
            numAppointments++;
            if (!types.contains(a.getType())) {
                types.add(a.getType());
            }
        }

        numAppointmentsLabel.setText(Integer.toString(numAppointments));
        monthComboBox.setItems(months);
        typeComboBox.setItems(types);
    }

    /**
     * Filters appointments by type.
     */
    @FXML
    void typeListener() {
        filterAppointments();
    }

    /**
     * Filters appointments by month.
     */
    @FXML
    void monthListener() {
        filterAppointments();
    }

    /**
     * Finds the number of appointments with a matching type and/or month
     * in respect to selection
     */
    void filterAppointments() {
        boolean typeExists = typeComboBox.getSelectionModel().getSelectedItem() != null;
        boolean monthExists = monthComboBox.getSelectionModel().getSelectedItem() != null;
        numAppointments = 0;

        if (typeExists && monthExists) {
            numAppointments = typeAndMonthMatching();
        } else if (typeExists) {
            numAppointments = typeMatching();
        } else if (monthExists) {
            numAppointments = monthMatching();
        }

        numAppointmentsLabel.setText(Integer.toString(numAppointments));
    }

    /**
     * @return the number of appointments matching the selected type and month.
     */
    int typeAndMonthMatching() {
        int i = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getType().equals(typeComboBox.getSelectionModel().getSelectedItem()) && a.getStartDateTime().getMonthValue() - 1 == months.indexOf(monthComboBox.getSelectionModel().getSelectedItem())) {
                i++;
            }
        }
        return i;
    }

    /**
     * @return the number of appointments matching the type selected by user.
     */
    int typeMatching() {
        int numAppointments = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getType().equals(typeComboBox.getSelectionModel().getSelectedItem())) {
                numAppointments++;
            }
        }
        return numAppointments;
    }

    /**
     * @return the number of appointments matching month selected by user.
     */
    int monthMatching() {
        int numAppointments = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if ((a.getStartDateTime().getMonthValue() - 1 == months.indexOf(monthComboBox.getSelectionModel().getSelectedItem()))) {
                numAppointments++;
            }
        }
        return numAppointments;
    }

    /**
     * Displays appointments view.
     *
     * @throws IOException
     */
    @FXML
    void backListener() throws IOException {
        System.out.println("Appointments");
        Main.setRoot("appointments");
    }
}
