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
        Scene scene = new Scene(fxmlLoader.load(), 450, 240);
        stage.setTitle("SQLSanitation");
        stage.setScene(scene);
        stage.setMinHeight(240);
        stage.setMinWidth(450);
        stage.show();
    }

    public static void main(String[] args)
    {
        //make sure database and ciphers were initialized properly before launching
        if (AESEncryption.AESEncryptionInitialization() && Database.databaseInitial())
        {
            launch();
        }else   //don't launch if something went wrong with initialization
        {
            System.out.println("Something went wrong with the database initialization");
        }
    }
}