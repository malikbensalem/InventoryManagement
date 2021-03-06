package com.qa.databases.daos;

import com.qa.databases.interfaces.CreateReturn;
import com.qa.databases.interfaces.Read;
import com.qa.databases.persistances.Utils;

import java.sql.*;
import java.util.InputMismatchException;

import org.apache.log4j.Logger;
/**
 * this class allows a connection between java and the Orders' table
 */

public class MySQLOrdersDAO implements CreateReturn, Read {

    private Connection connection;
    public static final Logger LOGGER = Logger.getLogger(MySQLOrdersDAO.class);
    

    /**
     * use this constructor if this is your first connection to the database during the run
     */

    public MySQLOrdersDAO() {
        LOGGER.info("User:");
        String name = Utils.INPUT.nextLine();
        LOGGER.info("password:");
        String pWord = Utils.INPUT.nextLine();
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://35.242.130.225/IMS", name, pWord);
        } catch (Exception e) {
            LOGGER.debug(e.getStackTrace());
        }
    }

    /**
     * use this constructor if you have already got access to the database
     *
     * @param name  - database name
     * @param pWord - database password
     */

    public MySQLOrdersDAO(String name, String pWord) {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://35.242.130.225/IMS", name, pWord);
        } catch (Exception e) {
            LOGGER.debug(e.getStackTrace());
        }
    }

    /**
     * closes this DAO down when called
     */

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            LOGGER.debug(e.getStackTrace());
        }
    }

    /**
     * updates the total price the order
     *
     * @param ordersID - this will be the id you want to set the totol for
     */
    public void addTotal(int ordersID) {//ordersid be where?
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "UPDATE Orders SET total= (SELECT sum(price) FROM OrderedItems, Items WHERE OrderedItems.ordersID = " + ordersID + ") WHERE id = " + ordersID);
            statement.executeUpdate("UPDATE Orders SET total = IF ( total>10000, total*9/10, total) WHERE id = " + ordersID);
        } catch (SQLException e) {
            LOGGER.debug(e.getStackTrace());
        }
    }

    /**
     * adds a new order into the database
     *
     * @return - orderID so the OrderItems' table can take multiple items with the same Orders' id
     */
    public int create() {
        int id = 0;
        try (Statement statement = connection.createStatement()) {
            LOGGER.info("customerID:");
            int customersID = Utils.INPUT2.nextInt();
            statement.executeUpdate("INSERT INTO Orders(customersID) VALUES(" + customersID + ");");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Orders");
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getStackTrace());
        }catch (InputMismatchException i) {
        	LOGGER.debug(i.getStackTrace());
        }
        return id;
    }

    /**
     * allows people to see all the orders that have been made
     * @return - Orders' table
     */
    public ResultSet read() {
        ResultSet resultSet = null;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement
                    .executeQuery("SELECT * FROM Orders;");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String custID = resultSet.getString("customersID");
                String total = resultSet.getString("total");
                LOGGER.info(id + " | " + custID + " | " + total);
            }
        } catch (Exception e) {
            LOGGER.debug(e.getStackTrace());
        }
        return resultSet;
    }
}
