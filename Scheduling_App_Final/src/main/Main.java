package main;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.ZoneId;

/**
 * The main class. It launches the application and controls which scenes/views
 * are shown.
 */
public class Main extends Application {

    /**
     * Create a new Scene
     */
    private static Scene scene;

    /**
     * Create a new Stage
     */
    private static Stage stage;

    /**
     * Create the zoneId
     */
    private static ZoneId zoneId = ZoneId.systemDefault();

    /**
     * The start method starts the stage and loads the login.fxml file.
     *
     * @param startStage the starting stage
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public void start(Stage startStage) throws IOException, SQLException {
        stage = startStage;
        stage.setTitle("Scheduling Application");
        setRoot("login");
    }

    /**
     * Sets a new scene using an fxml file string.
     *
     * @param fxml
     * @throws IOException
     */
    public static void setRoot(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml));

        try{scene.getStylesheets().add(Main.class.getResource("Project.css").toExternalForm());}
        catch(Exception e){System.out.println(e.fillInStackTrace());}
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Loads a new fxml file.
     *
     * @param fxml
     * @throws IOException
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * The main method launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Returns the ZoneId (timezone) of the user.
     *
     * @return the zoneId
     */
    public static ZoneId getZoneId() {
        return zoneId;
    }


}
