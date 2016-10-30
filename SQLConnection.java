import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
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
            handleException("FileNotFoundException: ", e);
        } catch (IOException e) {
            handleException("IOException: ", e);
        }

        //  Load the JDBC driver
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            handleException("ClassNotFoundException: ", e);
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
            handleException("SQLException: ", e);
        }
        System.out.println("Connected to mysql server");
    }

    public void executeQuery(String query) {
        Statement instruction;
        ResultSet resultat;
        try {
            instruction = connection.createStatement();
            resultat = instruction.executeQuery(query);
            int numColumns = resultat.getMetaData().getColumnCount();

            for (int i=1; i<=numColumns; i++) {
                System.out.print(resultat.getMetaData().getColumnLabel(i));
                if (i < numColumns) {
                    System.out.print(" | ");
                }
            }
            System.out.println("\n-----------------------------------------------");

            while (resultat.next()) {
                for (int i=1; i<=numColumns; i++) {
                    System.out.print(resultat.getString(i));
                    if (i < numColumns) {
                        System.out.print(" | ");
                    }
                }
                System.out.println();
            }
        } catch(SQLException e) {
            handleException("SQLException: ", e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            handleException("SQLException: ", e);
        }
    }

    private static void handleException(String message, Exception e) {
        System.err.println(message + e.getMessage());
        System.exit(1);
    }

    private static void waitForKeyPress() {
        System.out.println("Press Any Key to Continue...");
        try {
            System.in.read();
        } catch(IOException e) {
            handleException("IOException: ", e);
        }
    }

    public static void main(String[] args) {
        SQLConnection con = new SQLConnection();

        //  QUESTION 1
        String agent = "";
        String query = "";
        Scanner input = new Scanner(System.in);

        System.out.println("Enter an agents name to get the list of products sold by him/her");
        agent = input.nextLine();
        System.out.println();

        query = "SELECT pname FROM orders, products, agents " +
            "WHERE orders.pid=products.pid AND orders.aid=agents.aid AND " +
            "agents.aname=\"" + agent + "\";";

        con.executeQuery(query);


        //  QUESTION 2
        System.out.println("\nGetting the cheapest products from each city");
        waitForKeyPress();

        query = "SELECT city, pname, MIN(price) FROM products " +
            "GROUP BY city;";

        con.executeQuery(query);

        //  QUESTION 3
        String year = "";
        System.out.println("\nEnter in a year (1970-2016) to get the most expensive products ordered in each month of that year");
        year = input.nextLine();

        query = "SELECT month, pname, MAX(price) FROM orders NATURAL JOIN products " +
            "GROUP BY month;";

        con.executeQuery(query);

        //  QUESTION 4
        System.out.println("\nGetting the names of customers who's orders are always for products with prices over 1,000,000$");
        waitForKeyPress();

        query = "SELECT cname FROM customers " +
            "WHERE NOT EXISTS (SELECT * FROM orders, products WHERE orders.cid=customers.cid AND orders.pid=products.pid AND products.price <= 1000000) AND " +
            "EXISTS (SELECT * FROM orders WHERE orders.cid=customers.cid);";

        con.executeQuery(query);

        con.closeConnection();
    }
}
