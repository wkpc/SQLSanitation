package ss.sqlsanitation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Main extends Application
{
    private static Connection conn;
    private static ResultSet rs;
    private static Statement stmt;
    private static PreparedStatement pstmt;

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sqlsanitation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("SQLSanitation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        databaseInitial();
        launch();
    }

    /**
     * sets up the password databases with 2 tables, 1 for hashed passwords 1 for encrypted passwords
     */
    private static void databaseInitial()
    {
        try
        {
            System.out.println("doesn't exist");

            connect();

            //create the 2 tables for passwords (if they don't already exist), 1 for encrypted 1 for hashed
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS hashed ("
                    + "user TEXT PRIMARY KEY,"
                    + "password BINARY NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS encrypted ("
                    + "user TEXT PRIMARY KEY,"
                    + "password TEXT NOT NULL);");

            //check if the tables are empty (i.e. freshly created)
            rs = stmt.executeQuery(  "SELECT COUNT(*) FROM hashed;");

            rs.next();

            //if they are, add in the default passwords
            if (rs.getInt(1) == 0)
            {
                //add the default password to the hashed and encrypted table
                pstmt = conn.prepareStatement("INSERT INTO hashed(user, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, hash("password"));
                System.out.println(hash("2"));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("INSERT INTO encrypted(user, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, "two");
                pstmt.executeUpdate();
            }

            ResultSet rs = stmt.executeQuery(  "SELECT * FROM hashed");

            while (rs.next())
            {
                System.out.println(rs.getString("user") + ", " + rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * establishes a connection with the database
     */
    private static void connect() {
        // connection string
        String url = "jdbc:sqlite:database.db";

        try
        {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Given a plaintext string, hashes it with SHA-256 to get a binary string
     * @param plaintext The string to be hashed
     * @return The hashed version of the string, in binary. Returns empty string instead if SHA-256 couldn't be found.
     */
    public static String hash(String plaintext)
    {
        try
        {
            //use java's built in MessageDigest class for SHA-256 hash function
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));

            //now must convert hash into string for storage
            String hashString = "";

            //go through each byte in the hash...
            for (byte b: hash)
            {
                //...and through each bit in each hash...
                for (int i = 0; i < 8; i++)
                {
                    //collect each bit
                    hashString = hashString + ((b >> (7 - i)) & 1);
                }
            }

            return hashString;
        } catch (NoSuchAlgorithmException e) //if cannot find SHA-256 algorithm, return blank string
        {
            return "";
        }
    }

    /**
     * Given a set of login credentials, checks to see if login should be allowed. Uses Statements to query the database,
     * making it possible to sneak SQL commands into the inputs. Only possible for the SQL commands to be snuck into the
     * username field, since password input is first hashed before being passed into the query.
     * @param username The username credentials inputted by the user
     * @param password The password credentials inputted by the user
     * @param table The table to be checked for logins, either "hashed" or "encrypted"
     * @return true if a match is found, false if not
     */
    public static boolean unsanitizedLogin(String username, String password, String table)
    {
        try
        {
            //send a query to database to check for matches with username and password
            stmt = conn.createStatement();
            String command = "SELECT * FROM " + table +
                    " WHERE user = '" + username + "' AND password = '" +
                    password + "';";

            ResultSet rs = stmt.executeQuery(command);

            //check results of query, if at least one match was found allow access
            if (rs.next())
            {
                System.out.println("Access granted");
                return true;
            }else
            {
                return false;
            }
        } catch (SQLException e)    //if the input is invalid and breaks the query, assume invalid login credentials
        {
            return false;
        }
    }

    public static boolean sanitizedLogin(String username, String password, String table)
    {
        try
        {
            //send a query to database to check for matches with username and password
            pstmt = conn.prepareStatement("SELECT * FROM " + table +
                    " WHERE user = ? AND password = ?;");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            //check results of query, if at least one match was found allow access
            if (rs.next())
            {
                System.out.println("Access granted");
                return true;
            }else
            {
                return false;
            }
        } catch (SQLException e)     //if the input is invalid and breaks the query, assume invalid login credentials
        {
            return false;
        }
    }
}