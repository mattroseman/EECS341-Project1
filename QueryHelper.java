import java.sql.*

public class SQLConnection {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //  Load the JDBC driver
        Class.forName("org.mariadb.jdbc.Driver");
        System.out.println("Driver Loaded");

        //  Connecting to database
        Connection connection = DriverManager.getConnection("jdbc.mysql://localhost:3306", "root", "

    }
}
