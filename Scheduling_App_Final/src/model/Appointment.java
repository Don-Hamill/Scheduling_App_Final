package model;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;
import util.DBConnector;

/**
 * Appointment class allows the storing and manipulation of customer data.
 *
 */
public class Appointment {

    /**
     * A list containing every appointment.
     */
    static private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    private int id;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int customerId;
    private int userId;

    private String displayStartDateTime;
    private String displayEndDateTime;

    /**
     * Appointment constructor.
     *
     * @param id the id to set
     * @param title the title to set
     * @param description the description to set
     * @param location the location to set
     * @param contact the contact to set
     * @param type the appointment contact to set
     * @param startDateTime the startDateTime to set
     * @param endDateTime the endDateTime to set
     * @param customerId the customerId to set
     * @param userId the userId to set
     */
    public Appointment(int id,
                       String title,
                       String description,
                       String location,
                       String contact,
                       String type,
                       LocalDateTime startDateTime,
                       LocalDateTime endDateTime,
                       int customerId,
                       int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerId = customerId;
        this.userId = userId;


        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.of("UTC"));

        ZonedDateTime userStartDateTime = zonedStartDateTime.withZoneSameInstant(Main.getZoneId());
        ZonedDateTime userEndDateTime = zonedEndDateTime.withZoneSameInstant(Main.getZoneId());

        displayStartDateTime = userStartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        displayEndDateTime = userEndDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Set the appointment data.
     *
     * @param id the id to set
     * @param title the title to set
     * @param description the description to set
     * @param location the location to set
     * @param contact the contact to set
     * @param type the appointment contact to set
     * @param startDateTime the startDateTime to set
     * @param endDateTime the endDateTime to set
     * @param customerId the customerId to set
     * @param userId the userId to set
     */
    public void setAppointmentData(int id,
                                   String title,
                                   String description,
                                   String location,
                                   String contact,
                                   String type,
                                   LocalDateTime startDateTime,
                                   LocalDateTime endDateTime,
                                   int customerId,
                                   int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerId = customerId;
        this.userId = userId;

        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.of("UTC"));

        ZonedDateTime userStartDateTime = zonedStartDateTime.withZoneSameInstant(Main.getZoneId());
        ZonedDateTime userEndDateTime = zonedEndDateTime.withZoneSameInstant(Main.getZoneId());

        displayStartDateTime = userStartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        displayEndDateTime = userEndDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @return appointment id
     */
    public int getId() {
        return id;
    }

    /**
     * @return appointment title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return appointment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return appointment location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return appointment contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @return appointment type
     */
    public String getType() {
        return type;
    }

    /**
     * @return appointment start date/time in UTC
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * @return appointment end date/time in UTC
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * @return appointment start date/time string in users timezone for table display
     */
    public String getDisplayStartDateTime() {
        return displayStartDateTime;
    }

    /**
     * @return appointment end date/time string in users timezone for table display
     */
    public String getDisplayEndDateTime() {
        return displayEndDateTime;
    }

    /**
     * @return appointment start date in users timezone
     */
    public LocalDate getDisplayStartDate() {
        ZonedDateTime zonedStartDate = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userStartDate = zonedStartDate.withZoneSameInstant(Main.getZoneId());
        return userStartDate.toLocalDate();
    }

    /**
     * @return appointment end date in users timezone
     */
    public LocalDate getDisplayEndDate() {
        ZonedDateTime zonedEndDate = endDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userEndDate = zonedEndDate.withZoneSameInstant(Main.getZoneId());
        return userEndDate.toLocalDate();
    }

    /**
     * @return appointment start time string in users timezone
     */
    public String getDisplayStartTime() {
        ZonedDateTime zonedStartTime = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userStartTime = zonedStartTime.withZoneSameInstant(Main.getZoneId());
        String displayStartTime = userStartTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return displayStartTime;
    }

    /**
     * @return appointment end time string in users timezone
     */
    public String getDisplayEndTime() {
        ZonedDateTime zonedEndTime = endDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userEndTime = zonedEndTime.withZoneSameInstant(Main.getZoneId());
        String displayEndTime = userEndTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return displayEndTime;
    }

    /**
     * @return customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Adds a new appointment to appointmentList and database.
     *
     * @param appointment the appointment to add
     * @throws SQLException
     */
    static public void addAppointment(Appointment appointment) throws SQLException {
        appointmentList.add(appointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sqlStartDateTime = appointment.getStartDateTime().format(formatter);
        String sqlEndDateTime = appointment.getEndDateTime().format(formatter);
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + appointment.getContact() + "'");
        rs.next();
        DBConnector.executeUpdate("INSERT INTO appointments VALUES('"
                +appointment.getId()+"', '"
                +appointment.getTitle()+"', '"
                +appointment.getDescription()+"', '"
                +appointment.getLocation()+"', '"
                +appointment.getType()+"', '"
                +sqlStartDateTime+"', '"
                +sqlEndDateTime+"', '"
                +curTime+"', '"
                +DBConnector.getLoggedInUser()+"', '"
                +curTime+"', '"
                +DBConnector.getLoggedInUser()+"', '"
                +appointment.getCustomerId()+"', '"
                +appointment.getUserId()+"', '"
                +rs.getInt(1)+"')");

    }

    /**
     * Replaces old appointment in appointmentList and database with a new appointment.
     *
     * @param oldAppointment the appointment to be replaced
     * @param newAppointment the updated appointment
     * @throws SQLException
     */
    static public void updateAppointment(Appointment oldAppointment, Appointment newAppointment) throws SQLException {
        int index = appointmentList.indexOf(oldAppointment);
        appointmentList.set(index, newAppointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sqlStartDateTime = newAppointment.getStartDateTime().format(formatter);
        String sqlEndDateTime = newAppointment.getEndDateTime().format(formatter);
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + newAppointment.contact + "'");
        rs.next();
        DBConnector.executeUpdate("UPDATE appointments SET Title = '"+newAppointment.getTitle()
                +"', Description = '"+newAppointment.getDescription()
                +"', Location = '"+newAppointment.getLocation()
                +"', Type = '"+newAppointment.getType()
                +"', Start = '"+sqlStartDateTime
                +"', End = '"+sqlEndDateTime
                +"', Last_Update = '"+curTime
                +"', Last_Updated_By = '"+DBConnector.getLoggedInUser()
                +"', Customer_ID = '"+newAppointment.getCustomerId()
                +"', User_ID = '"+newAppointment.getUserId()
                +"', Contact_ID = '"+rs.getInt(1)+"' WHERE Appointment_ID = " + oldAppointment.getId());
    }

    /**
     * Removes an appointment from appointmentList if it exists in the list.
     *
     * @param appointment the appointment to remove
     * @throws SQLException
     */
    static public void deleteAppointment(Appointment appointment) throws SQLException {
        if (appointmentList.contains(appointment)) {
            int index = appointmentList.indexOf(appointment);
            DBConnector.executeUpdate("DELETE FROM appointments WHERE Appointment_ID = " + appointment.getId());
            appointmentList.remove(index);
        } else {
            System.out.println("Couldn't delete appointment; it doesn't exist in appointmentList.");
        }
    }

    /**
     * Adds an appointment to appointment list without updating the database.
     * Used for populating appointment table with database content.
     *
     * @param appointment the appointment to add.
     */
    static public void addAppointmentLocally(Appointment appointment) {
        appointmentList.add(appointment);
    }


    /**
     * Returns the list of all appointments.
     *
     * @return the appointmentList
     */
    static public ObservableList<Appointment> getAllAppointments() {
        return appointmentList;
    }

    /**
     * Clears the list of appointments
     * NOTE: DOES NOT UPDATE THE DATABASE; This method is for
     * clearing data when logging out of the application.
     */
    static public void clearAppointments() {
        appointmentList.clear();
    }


}
