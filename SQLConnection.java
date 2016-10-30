import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SQLConnection {
    private Properties config;
    private Connection connection;

    public SQLConnection() {
        //  Load configuration options
        try {
            config = new Properties();
            FileInputStream in = new FileInputStream("db.properties");
            config.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            HandleException("FileNotFoundException: ", e);
        } catch (IOException e) {
            HandleException("IOException: ", e);
        }

        //  Load the JDBC driver
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            HandleException("ClassNotFoundException: ", e);
        }
        System.out.println("Driver loaded");

        //  Connecting to database
        try {
            String server = "jdbc:mysql://" + config.getProperty("url") +
                            ":" + config.getProperty("port") + "/" +
                            config.getProperty("database");
            connection = DriverManager.getConnection(server, config.getProperty("user"), 
                                                     config.getProperty("password"));
        } catch (SQLException e) {
            HandleException("SQLException: ", e);
        }
        System.out.println("Connected to mysql server");
    }

    public void ExcecuteQuery(String query) {
        Statement instruction;
        ResultSet resultat;
        try {
            instruction = connection.createStatement();
            resultat = instruction.executeQuery(query);

            while (resultat.next()) {
                System.out.println(resultat.getString(1));
            }
        } catch(SQLException e) {
            HandleException("SQLException: ", e);
        }
    }

    public void CloseConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            HandleException("SQLException: ", e);
        }
    }

    private void HandleException(String message, Exception e) {
        System.err.println(message + e.getMessage());
        System.exit(1);
    }

    public static void main(String[] args) {
        SQLConnection con = new SQLConnection();

        con.ExcecuteQuery("SELECT COUNT(*) FROM orders;");

        con.CloseConnection();
    }
}
