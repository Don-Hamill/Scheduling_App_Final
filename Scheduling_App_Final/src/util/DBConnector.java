package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import main.Main;
import model.Appointment;
import model.Customer;

/**
 * The DBConnector class connects and disconnects from the database, performs queries
 * and database updates, stores the logged in user, and updates customer and
 * appointment data.
 *
 *
 */
public class DBConnector {

    static Connection dbLink;

    private static PreparedStatement statement;
    private static ResultSet results;
    private static String loggedInUser;
    private static int loggedInUserId;
    private static final String databaseName = "client_schedule";
    private static final String dbURL = "jdbc:mysql://localhost/" + databaseName;
    private static final String username = "sqlUser";
    private static final String password = "Passw0rd!";

    /**
     * Connects to the database.
     *
     * returns true or false
     */
    public static boolean connectToDB(){


        try {
            System.out.println("Connecting to database...");
            dbLink = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Database connection successful.");
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not connect to database.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        dbLink.close();
        System.out.println("Database connection closed.");
    }

    /**
     * Connects to the database.
     *
     * @param userName the username.
     * @param password the password.
     * @return whether the connection was successful.
     * @throws SQLException
     */
    public static boolean attemptLogin(String userName, String password) throws SQLException {
        executeQuery("SELECT User_Name, Password, User_ID FROM users WHERE User_Name = '" + userName + "' AND Password = '" + password + "'");

        if (results.next()) {
            loggedInUser = userName;
            loggedInUserId = results.getInt(3);
            return true;
        }
        return false;
    }

    /**
     * Executes a query and stores it in a result set.
     *
     * @param query the query to execute.
     * @throws SQLException
     */
    public static void executeQuery(String query) throws SQLException {
        if (statement != null) {statement.close();}
        if (results != null) {results.close();}

        try {
            statement = dbLink.prepareStatement(query);
            results = statement.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("SQL Query error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Query Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Executes a query and returns the results.
     *
     * @param query the query to execute.
     * @return the result set.
     * @throws SQLException
     */
    public static ResultSet executeQueryResults(String query) throws SQLException {
        if (statement != null) {statement.close();}
        if (results != null) {results.close();}

        try {
            statement = dbLink.prepareStatement(query);
            return statement.executeQuery(query);

        } catch (SQLException e) {
            System.out.println("SQL Query error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Query Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return null;
        }
    }

    /**
     * Executes an update on the database.
     *
     * @param update the SQL update to perform.
     * @throws SQLException
     */
    public static void executeUpdate(String update) throws SQLException {
        if (statement != null) {statement.close();}

        try {
            statement = dbLink.prepareStatement(update);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Update error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("SQL Update Error.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Populates the locally stored customers with updated data from the
     * database.
     *
     * @throws SQLException
     */
    public static void populateCustomers() throws SQLException {
        System.out.println("Obtaining customer data from database...");
        Customer.clearCustomers();

        int id;
        String name;
        String address;
        String postCode;
        String pNum;
        int countryId;
        String fLvlDiv;
        String country;

        PreparedStatement getCustomers = dbLink.prepareStatement("SELECT * FROM customers");
        ResultSet rs = getCustomers.executeQuery();

        while (rs.next()) {
            id = rs.getInt(1);
            name = rs.getString(2);
            address = rs.getString(3);
            postCode = rs.getString(4);
            pNum = rs.getString(5);

            ResultSet divs = executeQueryResults("SELECT Division, Country_ID FROM first_level_divisions WHERE Division_ID = " + rs.getInt(10));
            divs.next();
            fLvlDiv = divs.getString(1);
            countryId = divs.getInt(2);

            ResultSet countries = executeQueryResults("SELECT Country FROM countries WHERE Country_ID = " + countryId);
            countries.next();
            country = countries.getString(1);

            Customer customer = new Customer(id, name, country, fLvlDiv, address, postCode, pNum);
            Customer.addCustomerLocally(customer);
        }

        System.out.println("Successfully obtained customer data.");
    }

    /**
     * Populates the locally stored appointments with updated data from the
     * database.
     *
     * @throws SQLException
     */
    public static void populateAppts() throws SQLException {
        System.out.println("Obtaining appointment data from database...");
        Appointment.clearAppointments();

        int id;
        String title;
        String description;
        String location;
        String contact;
        String type;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        int customerId;
        int userId;

        PreparedStatement getAppts = dbLink.prepareStatement("SELECT * FROM appointments");
        ResultSet rs = getAppts.executeQuery();

        while (rs.next()) {
            id = rs.getInt(1);
            title = rs.getString(2);
            description = rs.getString(3);
            location = rs.getString(4);
            type = rs.getString(5);
            customerId = rs.getInt(12);
            userId = rs.getInt(13);

            ResultSet rsContacts = executeQueryResults("SELECT Contact_name FROM contacts WHERE Contact_ID = " + rs.getString(14));
            if (rsContacts.next()) {
                contact = rsContacts.getString(1);
            } else {
                contact = "NO CONTACT";
            }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(Main.getZoneId().toString()));
            startDateTime = LocalDateTime.parse(rs.getString(6), formatter);
            endDateTime = LocalDateTime.parse(rs.getString(7), formatter);

            Appointment appointment = new Appointment(id, title, description, location, contact, type, startDateTime, endDateTime, customerId, userId);
            Appointment.addAppointmentLocally(appointment);
        }

        System.out.println("Successfully obtained appointment data.");
    }

    /**
     * Returns the results stored by executeQuery().
     *
     * @return the current results stored.
     */
    public static ResultSet getResults() {
        return results;
    }

    /**
     * @return the current logged in user.
     */
    public static String getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * @return the current logged in user's ID.
     */
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    /**
     * Clears the currently logged in user.
     */
    public static void clearLoggedInUser() {
        loggedInUser = null;
        loggedInUserId = -1;
    }

}
