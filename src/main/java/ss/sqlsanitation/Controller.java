package ss.sqlsanitation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class Controller
{
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> sanitationMethodChoice;


    @FXML
    void initialize()
    {
        //populate the sanitation method choice box, and default to unsanitized
        sanitationMethodChoice.getItems().add("Unsanitized");
        sanitationMethodChoice.getItems().add("Sanitized");
        sanitationMethodChoice.getItems().add("Custom");
        sanitationMethodChoice.setValue("Unsanitized");
    }

    @FXML
    void onLoginPressed(ActionEvent event)
    {
        //collect the inputted username and password
        String inputUsername = usernameField.getText();
        String inputPassword = passwordField.getText();

        //choose the matching sanitation method and check for login
        if (sanitationMethodChoice.getValue().equals("Unsanitized"))
        {
            if (Database.unsanitizedLogin(inputUsername, Database.hash(inputPassword), "hashed"))
            {
                statusLabel.setText("Access granted");
            }else
            {
                statusLabel.setText("Access denied");
            }
        }else if (sanitationMethodChoice.getValue().equals("Sanitized"))
        {
            if (Database.sanitizedLogin(inputUsername, Database.hash(inputPassword), "hashed"))
            {
                statusLabel.setText("Access granted");
            }else
            {
                statusLabel.setText("Access denied");
            }
        }else
        {
            if (Database.customLogin(inputUsername, Database.hash(inputPassword), "hashed"))
            {
                statusLabel.setText("Access granted");
            }else
            {
                statusLabel.setText("Access denied");
            }
        }


    }
}
