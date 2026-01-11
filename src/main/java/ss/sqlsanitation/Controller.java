package ss.sqlsanitation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
    private TextArea databaseContents;

    private boolean accessGranted = false;

    /**
     * initial GUI set up
     */
    @FXML
    void initialize()
    {
        //populate the sanitation method choice box, and default to unsanitized
        sanitationMethodChoice.getItems().add("Unsanitized");
        sanitationMethodChoice.getItems().add("Sanitized");
        sanitationMethodChoice.getItems().add("Custom");
        sanitationMethodChoice.setValue("Unsanitized");

        //load the encrypted database contents
        databaseContents.setText(Database.printDatabase(false));
    }

    /**
     * Checks the inputted login credentials for a match with the password database, to see if access should be granted.
     * The method in which the SQL query is formed is determined by the login method chosen by the user.
     * @param event not used
     */
    @FXML
    void onLoginPressed(ActionEvent event)
    {
        //collect the inputted username and password
        String inputUsername = usernameField.getText();
        String inputPassword = passwordField.getText();

        //choose the matching sanitation method and check for login
        if (sanitationMethodChoice.getValue().equals("Unsanitized") &&
                Database.unsanitizedLogin(inputUsername, Database.hash(inputPassword), "hashed")
        || sanitationMethodChoice.getValue().equals("Sanitized") &&
                Database.sanitizedLogin(inputUsername, Database.hash(inputPassword), "hashed")
        || sanitationMethodChoice.getValue().equals("Custom") &&
                Database.customLogin(inputUsername, Database.hash(inputPassword), "hashed"))
        {
            statusLabel.setText("Access granted");
            accessGranted = true;
        }else
        {
            statusLabel.setText("Access denied");
        }
    }

    /**
     * Check if user has successfully signed in, and if so, decrypts the contents of the database.
     * @param event Not used
     */
    @FXML
    void onDecryptDataPressed(ActionEvent event)
    {
        //check if the user has access, and decrypt the database if they do
        if (accessGranted)
        {
            databaseContents.setText(Database.printDatabase(true));
        }else
        {
            statusLabel.setText("Permission Denied");
        }
    }
}
