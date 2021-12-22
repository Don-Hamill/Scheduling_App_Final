package controller;

import javafx.scene.control.*;
import main.Main;
import model.Customer;
import java.io.IOException;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import util.DBConnector;

/**
 * The controller class for the customers view.
 */
public class CustomersController {

    int numErrors;
    private final ObservableList<String> countries = FXCollections.observableArrayList();
    private final ObservableList<String> firstLevelDivisions = FXCollections.observableArrayList();

    @FXML private TableView<Customer> customerTable;

    @FXML private TableColumn<Customer, Integer> customerIdCol;

    @FXML private TableColumn<Customer, String> nameCol;

    @FXML private TableColumn<Customer, String> countryCol;

    @FXML private TableColumn<Customer, String> firstLvlDivCol;

    @FXML private TableColumn<Customer, String> addressCol;

    @FXML private TableColumn<Customer, String> postalCodeCol;

    @FXML private TableColumn<Customer, String> phoneNumCol;

    @FXML private TextField idField;

    @FXML private TextField postalCodeField;

    @FXML private TextField nameField;

    @FXML private TextField addressField;

    @FXML private TextField phoneNumField;

    @FXML private ComboBox<String> countryComboBox;

    @FXML private ComboBox<String> firstLvlDivComboBox;

    @FXML private Text firstLvlDivText;

    /**
     * LAMBDA EXPRESSION: An event listener that checks if an item was selected,
     * and populates fields with the customer's data if it was.
     *
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException {
        System.out.println("Initialize customer records");

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        firstLvlDivCol.setCellValueFactory(new PropertyValueFactory<>("firstLvlDiv"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        customerTable.setItems(Customer.getAllCustomers());

        DBConnector.executeQuery("SELECT country FROM countries");
        while (DBConnector.getResults().next()) {
            countries.add(DBConnector.getResults().getString(1));
        }
        countryComboBox.setItems(countries);

        //Lambda expression
        customerTable.getSelectionModel().selectedItemProperty().addListener((newSelection) -> {
            if (newSelection != null && getSelection() != null) {
                populateFields(getSelection());
            } else {
                clearFields();
            }
        });
    }

    /**
     * Gets the selected item in the customers table.
     *
     * @return the selected customer.
     */
    Customer getSelection() {
        return customerTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Populates the fields for updating a customer.
     *
     * @param customer the customer with data to use.
     */
    void populateFields(Customer customer) {
        System.out.println("Selected Customer: " + customer.getName());
        idField.setText(Integer.toString(customer.getId()));
        nameField.setText(customer.getName());
        countryComboBox.setValue(customer.getCountry());
        firstLvlDivComboBox.setValue(customer.getFirstLvlDiv());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        phoneNumField.setText(customer.getPhoneNum());
    }

    /**
     * Clears every field.
     */
    void clearFields() {
        if (getSelection() != null) {
            customerTable.getSelectionModel().clearSelection();
        }
        idField.clear();
        nameField.clear();
        countryComboBox.setValue(null);
        firstLvlDivComboBox.setValue(null);
        addressField.clear();
        postalCodeField.clear();
        phoneNumField.clear();
    }

    /**
     * Sets the items of the first level divisions comboBox
     *
     * @throws SQLException
     */
    @FXML
    void updateCountryListener() throws SQLException {
        firstLvlDivComboBox.getItems().clear();
        firstLevelDivisions.removeAll();
        if (countryComboBox.getValue() != null) {
            DBConnector.executeQuery("SELECT Country_ID FROM countries WHERE Country = '" + countryComboBox.getValue() + "'");

            if (DBConnector.getResults().next()) {
                int countryId = (DBConnector.getResults().getInt(1));
                DBConnector.executeQuery("SELECT Division, COUNTRY_ID FROM first_level_divisions WHERE COUNTRY_ID = " + countryId);
                while (DBConnector.getResults().next()) {
                    firstLevelDivisions.add(DBConnector.getResults().getString(1));
                }
                firstLvlDivComboBox.setItems(firstLevelDivisions);
            } else {
                System.out.println("Query was not executed correctly.");
            }
        }
    }

    /**
     * Updates the selected customer with the data from each field if
     * they are all valid.
     *
     * @throws SQLException
     */
    @FXML
    void updateListener() throws SQLException {
        System.out.println("Update customer");

        if (getSelection() != null) {
            if (fieldsValid()) {
                int id = getSelection().getId();
                String name = nameField.getText();
                String country = countryComboBox.getValue();
                String firstLvlDiv = firstLvlDivComboBox.getValue();
                String address = addressField.getText();
                String postalCode = postalCodeField.getText();
                String phoneNum = phoneNumField.getText();

                Customer newCustomer = new Customer(id, name, country, firstLvlDiv, address, postalCode, phoneNum);
                Customer.updateCustomer(getSelection(), newCustomer);

                customerTable.setItems(Customer.getAllCustomers());
                clearFields();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer updated: " + newCustomer.getName());
                alert.setTitle("Error");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select a customer to update.");
            alert.setTitle("Error");
        }
    }

    /**
     * Adds a new customer with the data from each of the fields if they are
     * valid.
     *
     * @throws SQLException
     */
    @FXML
    void addListener() throws SQLException {
        System.out.println("Add customer");

        if (idField.getText().isBlank()) {
            if (fieldsValid()) {
                int id = getNewId();
                String name = nameField.getText();
                String country = countryComboBox.getValue();
                String firstLvlDiv = firstLvlDivComboBox.getValue();
                String address = addressField.getText();
                String postalCode = postalCodeField.getText();
                String phoneNum = phoneNumField.getText();

                Customer newCustomer = new Customer(id, name, country, firstLvlDiv, address, postalCode, phoneNum);
                Customer.addCustomer(newCustomer);

                customerTable.setItems(Customer.getAllCustomers());
                clearFields();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer added: " + newCustomer.getName());
                alert.setTitle("Error");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Could not add customer; ID " + idField.getText() + " already exists.");
            alert.setTitle("Error");
        }
    }

    /**
     * Deletes the selected customer.
     *
     * @throws SQLException
     */
    @FXML
    void deleteListener() throws SQLException {
        System.out.println("Delete customer");

        if (getSelection() != null) {
            String name = getSelection().getName();
            int id = getSelection().getId();
            Customer.deleteCustomer(getSelection());
            customerTable.setItems(Customer.getAllCustomers());
            clearFields();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer deleted: " + name);
            alert.setTitle("Error");
            System.out.println("Deleted customer " + name + " with ID " + id);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select a customer to delete.");
            alert.setTitle("Error");
        }
    }

    /**
     * Changes to the main menu view.
     *
     * @throws IOException
     */
    @FXML
    void backListener() throws IOException {
        System.out.println("Main menu");
        Main.setRoot("menu");
    }

    /**
     * Clears the current selection in the table view.
     */
    @FXML
    void clearSelectionListener() {
        System.out.println("Clear selection");
        if (getSelection() != null) {
            customerTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Checks whether the values in each field are valid.
     *
     * @return whether each field has a valid value.
     */
    Boolean fieldsValid() {
        boolean rVal = true;
        String error = "";
        numErrors = 0;

        if (phoneNumField.getText().isBlank()) {
            error = "Phone number field";
            numErrors++;
            rVal = false;
        }
        if (postalCodeField.getText().isBlank()) {
            error = "Postal code field";
            numErrors++;
            rVal = false;
        }
        if (addressField.getText().isBlank()) {
            error = "Address field";
            numErrors++;
            rVal = false;
        }
        if (firstLvlDivComboBox.getValue() == null) {
            error = firstLvlDivText.getText().replace(':', ' ');
            numErrors++;
            rVal = false;
        }
        if (countryComboBox.getValue() == null) {
            error = "Country box";
            numErrors++;
            rVal = false;
        }
        if (nameField.getText().isBlank()) {
            error = "Name field";
            numErrors++;
            rVal = false;
        }

        if (numErrors == 1){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, error + " is empty.");
            alert.setTitle("Error");
        } else if (numErrors > 1){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, error + " is empty  + " + (numErrors - 1) + " more errors.");
            alert.setTitle("Error");
        }

        return rVal;
    }

    /**
     * Returns a new customer id.
     *
     * @return a new customer id.
     */
    int getNewId() {
        int max = 0;
        for (Customer c : Customer.getAllCustomers()) {
            if (c.getId() > max) {
                max = c.getId();
            }
        }
        return max + 1;
    }
}