package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.Main;
import model.Customer;
import model.Appointment;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import util.DBConnector;

/**
 * The controller class for the main menu view.
 *
 */
public class MenuController {

    @FXML
    private Label alertLabel;

    /**
     * The initialize method populates the appointments and customer lists with
     * updated data and sets the text of the 15 minute alert label.
     *
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException{
        DBConnector.populateAppts();
        DBConnector.populateCustomers();
        LocalDateTime startTime;

        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");

        ArrayList<String> appointmentsSoon = new ArrayList<>();
        for (Appointment a : Appointment.getAllAppointments()) {
            startTime = a.getStartDateTime();
            if (curTime.isBefore(startTime) && curTime.isAfter(startTime.minusMinutes(15))) {
                ZonedDateTime userZonedDateTime = startTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(Main.getZoneId());
                appointmentsSoon.add(userZonedDateTime.format(formatter) + " - ID: " + a.getId());

            }
        }

        if (!appointmentsSoon.isEmpty()) {

            for (String s : appointmentsSoon) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointments within 15 minutes: " + s);
                alert.setTitle("Upcoming appointments");
                Optional<ButtonType> result = alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"No appointments in next 15 minutes.");
            alert.setTitle("No upcoming appointments");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /**
     * Changes to the appointments view.
     *
     * @throws IOException
     */
    @FXML
    void viewApptHandler() throws IOException {
        Main.setRoot("appointments");
    }

    /**
     * Changes to the customers view.
     *
     * @throws IOException
     */
    @FXML
    void viewCustomersHandler() throws IOException {
        Main.setRoot("customers");
    }

    /**
     * Changes to the login view, clears the appointments and customers locally,
     * and closes the database connection.
     *
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    void logoutHandler() throws IOException, SQLException {
        Main.setRoot("login");
        Appointment.clearAppointments();
        Customer.clearCustomers();
        DBConnector.clearLoggedInUser();
        DBConnector.closeConnection();
    }

    /**
     * Closes the database application and closes the application.
     *
     * @throws SQLException
     */
    @FXML
    void quitHandler() throws SQLException {
        System.out.println("Quitting application...");
        DBConnector.closeConnection();
        System.exit(0);
    }
}
