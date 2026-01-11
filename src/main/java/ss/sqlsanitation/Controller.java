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

    @FXML
    private TextField addEntryKeyField;

    @FXML
    private TextField addEntryDataField;

    @FXML
    private TextField removeEntryKeyField;

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

    /**
     * If the user has signed in, adds a new row to the encrypted table in database.db. Uses the key and data values
     * provided in the associated text fields. If the user hasn't signed in, or either of the text fields are left blank,
     * do nothing.
     * @param event Not used
     */
    @FXML
    void onAddData(ActionEvent event)
    {
        //check if the addDataField has been filled out and user has login permission
        if (accessGranted && !addEntryKeyField.getText().isBlank() && !addEntryDataField.getText().isBlank())
        {
            //if it has, add the entry and update the database display
            boolean success = Database.addEntry(addEntryKeyField.getText(), addEntryDataField.getText());
            databaseContents.setText(Database.printDatabase(false));

            //notify user of action success
            if (success)
            {
                statusLabel.setText("Entry added");
            }else
            {
                statusLabel.setText("Entry could not be added.");
            }
        }else
        {
            statusLabel.setText("Entry could not be added.");
        }
    }

    /**
     * If the user has signed in, removes a row from the encrypted table in database.db. Uses the key value provided in
     * the associated text fields, and delete all entries with matching key values (Keys are unique, so at most 1 row is
     * deleted). If the user hasn't signed in, or the text fields is left blank, do nothing.
     * @param event Not used
     */
    @FXML
    void OnRemoveData(ActionEvent event)
    {
        //check if the addDataField has been filled out and user has login permission
        if (accessGranted && !removeEntryKeyField.getText().isBlank())
        {
            //if it has, add the entry and update the database display
            boolean success = Database.removeEntry(removeEntryKeyField.getText());
            databaseContents.setText(Database.printDatabase(false));

            //notify user of action success
            if (success)
            {
                statusLabel.setText("Entry removed");
            }else
            {
                statusLabel.setText("Entry could not be removed.");
            }
        }else
        {
            statusLabel.setText("Entry could not be removed.");
        }
    }
}
