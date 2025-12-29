package ss.sqlsanitation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick()
    {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}