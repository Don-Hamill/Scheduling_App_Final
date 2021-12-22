package controller;

import java.sql.SQLException;
import java.time.format.TextStyle;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import main.Main;
import util.DBConnector;

/**
 * The controller class for the login view.
 */
public class LoginController {

    Locale location = Locale.getDefault();
    ResourceBundle myResources = ResourceBundle.getBundle("lang/localization", location);
    Boolean databaseConnected = false;

    @FXML private TextField userIdField;

    @FXML private PasswordField passwordField;

    @FXML private Button loginButton;

    @FXML private Label errorLabel;

    @FXML private Label locationLabel;

    @FXML private Label welcomeLabel;

    @FXML private Label userIdLabel;

    @FXML private Label passwordLabel;

    @FXML private Label currentLocationLabel;

    /**
     * The initialize method sets the text of the login screen using the users
     * location to determine the language.
     *
     */
    @FXML
    void initialize(){
        System.out.println("Initialize login.");

        welcomeLabel.setText(myResources.getString("welcomeLbl"));
        userIdLabel.setText(myResources.getString("userIdLbl"));
        userIdField.setPromptText(myResources.getString("userPromptText"));
        passwordLabel.setText(myResources.getString("passwordLbl"));
        passwordField.setPromptText(myResources.getString("passwordPromptText"));
        loginButton.setText(myResources.getString("loginBtn"));
        currentLocationLabel.setText(myResources.getString("yourLocLbl"));

        locationLabel.setText(Main.getZoneId().getDisplayName(TextStyle.FULL, location));
    }

    /**
     * Logs in the user if credentials match those stored in the database.
     * Connects to the database if not connected.
     * Saves login information to a text file in the root folder.
     *
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    void loginListener() throws IOException, SQLException {
        System.out.println("Attempting login...");
        String userId = userIdField.getText();
        String password = passwordField.getText();
        boolean successfulLogin = false;
        String loginState;
        userIdField.clear();
        passwordField.clear();

        if (!databaseConnected) {
            if (DBConnector.connectToDB()) {
                databaseConnected = true;
            }
        }

        if (databaseConnected && DBConnector.attemptLogin(userId, password)) {
            Main.setRoot("menu");

            successfulLogin = true;
            System.out.println("Login successful.");
        } else {
            errorLabel.setText(myResources.getString("errorLbl"));
            System.out.println("Login failed.");
        }

        try {
            File newFile = new File("login_activity.txt");
            if (newFile.createNewFile()) {
                System.out.println("New login activity file created.");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();

            if (successfulLogin) {
                loginState = "Successful";
            } else {
                loginState = "Failed";
            }

            FileWriter fWriter = new FileWriter(newFile, true);
            fWriter.write("\n" + formatter.format(currentTime) + " - " + loginState + " Login Attempt. Username: '" + userId + "'");
            fWriter.close();

        } catch (IOException e) {
            System.out.println("Login activity text file error: " + e.getMessage());
        }
    }
}
