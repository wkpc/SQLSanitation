package ss.sqlsanitation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Controller
{
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    void onLoginPressed(ActionEvent event)
    {
        String inputUsername = usernameField.getText();
        String inputPassword = passwordField.getText();
    }
}
