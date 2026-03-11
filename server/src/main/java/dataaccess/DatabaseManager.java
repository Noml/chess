package dataaccess;

import server.Database;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    public DatabaseManager() throws DataAccessException{
        createDatabase();
        createTables();
    }
    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }
    static public void createTables() throws DataAccessException{
        var createAuthDataTable = """
            CREATE TABLE IF NOT EXISTS authData (
                id INT NOT NULL AUTO_INCREMENT,
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
            )""";
        var createGameDataTable = """
            CREATE TABLE  IF NOT EXISTS gameData (
                id INT NOT NULL AUTO_INCREMENT,
                gameID INT NOT NULL,
                whiteUsername VARCHAR(225),
                blackUsername VARCHAR(255),
                gameName VARCHAR(225) NOT NULL,
                ChessGame JSON NOT NULL,
                PRIMARY KEY (id)
            )""";
        var createUserDataTable = """
            CREATE TABLE  IF NOT EXISTS userData (
                id INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(225) NOT NULL,
                password VARCHAR(225) NOT NULL,
                email VARCHAR(225) NOT NULL,
                PRIMARY KEY (id)
            )""";
        Connection conn = getConnection();
        try (var createTableStatement = conn.prepareStatement(createAuthDataTable);
             var createTableStatement2 = conn.prepareStatement(createGameDataTable);
             var createTableStatement3 = conn.prepareStatement(createUserDataTable)) {
            createTableStatement.executeUpdate();
            createTableStatement2.executeUpdate();
            createTableStatement3.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables",e);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    public void deleteData(DatabaseManager.DataType type) throws DataAccessException {
        Connection conn = getConnection();
        switch (type){
            case GAMEDATA:
                try (var preparedStatement = conn.prepareStatement("DELETE FROM gameData")) {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Error deleting gameData",e);
                }
            case AUTHDATA:
                try (var preparedStatement = conn.prepareStatement("DELETE FROM authData")) {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Error deleting authData",e);
                }
            case USERDATA:
                try (var preparedStatement = conn.prepareStatement("DELETE FROM userData")) {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Error deleting userData",e);
                }
        }

    }

    public enum DataType{
        GAMEDATA,
        AUTHDATA,
        USERDATA
    }
}
