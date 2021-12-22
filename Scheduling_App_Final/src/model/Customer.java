package model;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBConnector;

/**
 * Customer class allows the storing and manipulation of customer data.
 */
public class Customer {


    /**
     * A list containing every customer.
     */
    static private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private int id;
    private String name;
    private String country;
    private String firstLvlDiv;
    private String address;
    private String postalCode;
    private String phoneNum;

    /**
     * Customer constructor.
     *
     * @param id the id to set
     * @param name the name to set
     * @param country the country to set
     * @param firstLvlDiv the first-level division to set
     * @param address the address to set
     * @param postalCode the postal code to set
     * @param phoneNum the phone number to set
     */
    public Customer(int id,
                    String name,
                    String country,
                    String firstLvlDiv,
                    String address,
                    String postalCode,
                    String phoneNum) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.firstLvlDiv = firstLvlDiv;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;

    }

    /**
     * Set the customer data.
     *
     * @param id the id to set
     * @param name the name to set
     * @param country the country to set
     * @param firstLvlDiv the first-level division to set
     * @param address the address to set
     * @param postalCode the postal code to set
     * @param phoneNum the phone number to set
     */
    public void setCustomerData(int id,
                                String name,
                                String country,
                                String firstLvlDiv,
                                String address,
                                String postalCode,
                                String phoneNum) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.firstLvlDiv = firstLvlDiv;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
    }

    /**
     * @return customer id
     */
    public int getId() {
        return id;
    }

    /**
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     * @return customer country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return customer first-level division
     */
    public String getFirstLvlDiv() {
        return firstLvlDiv;
    }

    /**
     * @return customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return customer postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return customer phone number
     */
    public String getPhoneNum() {
        return phoneNum;
    }


    //CUSTOMER STATIC METHODS THAT OPERATE WITH CUSTOMER LIST

    /**
     * Adds a new customer to customerList.
     *
     * @param customer the customer to add
     * @throws SQLException
     */
    static public void addCustomer(Customer customer) throws SQLException {
        customerList.add(customer);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Division_ID FROM first_level_divisions WHERE division = '" + customer.getFirstLvlDiv() + "'");
        rs.next();
        DBConnector.executeUpdate("INSERT INTO customers VALUES('"
                +customer.getId()+"', '"
                +customer.getName()+"', '"
                +customer.getAddress()+"', '"
                +customer.getPostalCode()+"', '"
                +customer.getPhoneNum()+"', '"
                +curTime+"', '"
                +DBConnector.getLoggedInUser()+"', '"
                +curTime+"', '"
                +DBConnector.getLoggedInUser()+"', '"
                +rs.getInt(1)+"')");
    }



    /**
     * Replaces old customer in customerList with a new customer.
     *
     * @param oldCustomer the customer to be replaced
     * @param newCustomer the customer to replace oldCustomer
     * @throws SQLException
     */
    static public void updateCustomer(Customer oldCustomer, Customer newCustomer) throws SQLException {
        int index = customerList.indexOf(oldCustomer);
        customerList.set(index, newCustomer);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Division_ID FROM first_level_divisions WHERE division = '" + newCustomer.getFirstLvlDiv() + "'");
        rs.next();
        DBConnector.executeUpdate("UPDATE customers SET Customer_Name = '"+newCustomer.getName()
                +"', Address = '"+newCustomer.getAddress()
                +"', Postal_Code = '"+newCustomer.getPostalCode()
                +"', Phone = '"+newCustomer.getPhoneNum()
                +"', Last_Update = '"+curTime
                +"', Last_Updated_By = '"+DBConnector.getLoggedInUser()
                +"', Division_ID = '"+rs.getInt(1)+ "' WHERE Customer_ID = " + oldCustomer.getId());
    }

    /**
     * Removes a customer from customerList if it exists in the list.
     *
     * @param customer the customer to remove
     * @throws SQLException
     */
    static public void deleteCustomer(Customer customer) throws SQLException {
        if (customerList.contains(customer)) {
            int index = customerList.indexOf(customer);
            customerList.remove(index);

            DBConnector.executeUpdate("DELETE FROM appointments WHERE Customer_ID = " + customer.getId());
            DBConnector.executeUpdate("DELETE FROM customers WHERE Customer_ID = " + customer.getId());
        } else {
            System.out.println("Couldn't delete customer; it doesn't exist in customerList.");
        }
    }

    /**
     * Adds a customer to customer list without updating the database.
     * Used for populating customer table with database content.
     *
     * @param customer the appointment to add.
     */
    static public void addCustomerLocally(Customer customer) {
        customerList.add(customer);
    }

    /**
     * Returns a reference to the list of all customers.
     *
     * @return the customerList
     */
    static public ObservableList<Customer> getAllCustomers() {
        return customerList;
    }

    /**
     * Clears the list of customers
     * NOTE: DOES NOT UPDATE SQL TABLES; This method is for
     * clearing data when logging out of the application.
     */
    static public void clearCustomers() {
        customerList.clear();
    }


}