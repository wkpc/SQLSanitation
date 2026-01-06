package ss.sqlsanitation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public final class Database
{
    private static Connection conn;
    private static Statement stmt;

    /**
     * sets up the password databases with 2 tables, 1 for hashed passwords 1 for encrypted passwords
     * @return true if initialization suceeds, false if an error is encountered
     */
    public static boolean databaseInitial()
    {
        try
        {
            //connect to the database
            connect();

            //create the 2 tables for passwords (if they don't already exist), 1 for encrypted 1 for hashed
            stmt.execute("CREATE TABLE IF NOT EXISTS hashed ("
                    + "user TEXT PRIMARY KEY,"
                    + "password BINARY NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS encrypted ("
                    + "user TEXT PRIMARY KEY,"
                    + "password TEXT NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS test ("
                    + "user TEXT PRIMARY KEY,"
                    + "password TEXT NOT NULL);");

            //check if the tables are empty (i.e. freshly created)
            ResultSet rs = stmt.executeQuery(  "SELECT COUNT(*) FROM hashed;");

            rs.next();

            //if they are, add in the default passwords
            if (rs.getInt(1) == 0)
            {
                //add the default password to the hashed and encrypted table
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO hashed(user, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, hash("password"));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("INSERT INTO encrypted(user, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, "two");
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("INSERT INTO test(user, password) VALUES (?, ?)");
                pstmt.setString(1, "admin");
                pstmt.setString(2, "two");
                pstmt.executeUpdate();
            }

            /// /////////////////////////////////// DEBUG ///////////////////////////////////
            rs = stmt.executeQuery(  "SELECT * FROM hashed");

            while (rs.next())
            {
                System.out.println(rs.getString("user") + ", " + rs.getString("password"));
            }

            //return true if initialization suceeded
            return true;
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
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

            //also set up stmt for later use
            stmt = conn.createStatement();
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
            //use StringBuilder instead of string for repeated concatenation
            StringBuilder hashString = new StringBuilder();

            //go through each byte in the hash...
            for (byte b: hash)
            {
                //and convert it into a hexadecimal string
                hashString.append(String.format("%02x", b));
            }

            System.out.println("Hash: " + hashString);
            return hashString.toString();
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

    /**
     * Given a set of login credentials, checks to see if login should be allowed. Uses PreparedStatements to query the
     * database, using java's built-in functions for input sanitization.
     * @param username The username credentials inputted by the user
     * @param password The password credentials inputted by the user
     * @param table The table to be checked for logins, either "hashed" or "encrypted"
     * @return true if a match is found, false if not
     */
    public static boolean sanitizedLogin(String username, String password, String table)
    {
        try
        {
            //send a query to database to check for matches with username and password
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + table +
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

    /**
     * Given a set of login credentials, checks to see if login should be allowed. Uses Statements to query the database,
     * which are susceptible to SQL injections. This is a modified version of the unsanitized login method, with the
     * addition of brackets around each of the conditions. This makes it so it is impossible to circumvent one of the
     * conditions (i.e. both conditions must be satisfied), so SQL commands would have to be injected into both fields
     * to satisfy the conditions. This shouldn't be possible, as the password field is hashed before being passed to the
     * database, thus destroying any possible SQL commands. It would only be possible for SQL commands to be snuck into
     * the username field, so this should be relatively safe from SQL injections. In addition, the escaper() function is
     * called on the input before being passed to as a query, to sanitize the input.
     * @param username The username credentials inputted by the user
     * @param password The password credentials inputted by the user
     * @param table The table to be checked for logins, either "hashed" or "encrypted"
     * @return true if a match is found, false if not
     */
    public static boolean customLogin(String username, String password, String table)
    {
        try
        {
            username = escaper(username);
            //password doesn't need to be sanitized, already hashed

            //send a query to database to check for matches with username and password
            String command = "SELECT * FROM " + table +
                    " WHERE (user = '" + username + "') AND (password = '" +
                    password + "');";

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

    /**
     * Special characters ', ", and \ are escaped to ensure input is treated a literal, and prevent string escaping.
     * @param input The string to be sanitized
     * @return The sanitized string, enclosed in ""
     */
    private static String escaper(String input)
    {
        //escape possibly problematic special characters
        input = input.replace("\\", "\\\\");
        input = input.replace("'", "''");
        input = input.replace("\"", "\"\"");

        //enclose entire string in "", so SQL treats it like a literal
        System.out.println("\"" + input + "\"");
        return input;
    }
}
