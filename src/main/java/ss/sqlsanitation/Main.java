package ss.sqlsanitation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        stage.setTitle("SQLSanitation");
        stage.setScene(scene);
        stage.setMinHeight(240);
        stage.setMinWidth(400);
        stage.show();
    }

    public static void main(String[] args)
    {
        //make sure database was initialized properly before launching
        if (Database.databaseInitial())
        {
            System.out.println(AESEncryption.encryptAES("hello"));
            launch();
        }else
        {
            System.out.println("Something went wrong with the database initialization");
        }

        String encHello = AESEncryption.encryptAES("hello");
        System.out.println("Encrypted hello: " + encHello);
        System.out.println("encryption test: " + AESEncryption.decryptAES(encHello));
    }
}