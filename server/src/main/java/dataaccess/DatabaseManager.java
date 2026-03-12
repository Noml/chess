package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.Assertions;
import server.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;
    private Gson gson;

    public DatabaseManager() throws DataAccessException{
        createDatabase();
        createTables();
        gson = new Gson();
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
             var createTableStatement3 = conn.prepareStatement(createUserDataTable)){
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


    public ArrayList<UserData> getAllUserData() throws DataAccessException{
        Connection conn = getConnection();
        ArrayList<UserData> allUserData = new ArrayList<>();
        try(var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM userData")){
            try(var rs = preparedStatement.executeQuery()){
                while(rs.next()){
                    var username = rs.getString("username");
                    var password = rs.getString("password");
                    var email = rs.getString("email");
                    var userData = new UserData(username,password,email);
                    allUserData.add(userData);
                }
            }
            return allUserData;
        }catch(SQLException e){
            throw new DataAccessException("Error getting data from table userData",e);
        }
    }

    public ArrayList<AuthData> getAllAuthData() throws DataAccessException{
        Connection conn = getConnection();
        ArrayList<AuthData> allAuthData = new ArrayList<>();
        try(var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM authData")){
            try(var rs = preparedStatement.executeQuery()){
                while(rs.next()){
                    var authToken = rs.getString("authToken");
                    var username = rs.getString("username");
                    var authData = new AuthData(authToken,username);
                    allAuthData.add(authData);
                }
            }
            return allAuthData;
        }catch(SQLException e){
            throw new DataAccessException("Error getting data from table authData",e);
        }
    }

    public ArrayList<GameData> getAllGameData() throws DataAccessException{
        Connection conn = getConnection();

        ArrayList<GameData> allGameData = new ArrayList<>();
        try(var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername,blackUsername,gameName,ChessGame FROM gameData")){
            try(var rs = preparedStatement.executeQuery()){
                while(rs.next()){
                    var gameID = rs.getInt("gameID");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    ChessGame chessGame = gson.fromJson(rs.getString("ChessGame"),ChessGame.class);
                    var gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);
                    allGameData.add(gameData);
                }
            }
            return allGameData;
        }catch(SQLException e){
            throw new DataAccessException("Error getting data from table gameData",e);
        }
    }

    public void addUserData(UserData u) throws DataAccessException{
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO userData (username, password, email) VALUES(?, ?, ?)")) {
            preparedStatement.setString(1, u.username());
            preparedStatement.setString(2, u.password());
            preparedStatement.setString(3, u.email());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error adding UserData",e);
        }
    }

    public void addGameData(GameData g) throws DataAccessException{
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO gameData (gameID,whiteUsername,blackUsername,gameName,ChessGame) VALUES(?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, g.gameID());
            preparedStatement.setString(2, g.whiteUsername());
            preparedStatement.setString(3, g.blackUsername());
            preparedStatement.setString(4, g.gameName());

            var chessGame = gson.toJson(g.game());
            preparedStatement.setString(5, chessGame);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error adding GameData",e);
        }
    }

    public void addAuthData(AuthData a) throws DataAccessException{
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO authData (authToken,username) VALUES(?, ?)")) {
            preparedStatement.setString(1, a.authToken());
            preparedStatement.setString(2, a.username());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error adding AuthData",e);
        }

    }

    public void deleteData(String authToken) throws DataAccessException {
        Connection conn = getConnection();
        var statement = "DELETE from authData WHERE authToken=?";
        try(var prepStatement = conn.prepareStatement(statement)){
            prepStatement.setString(1, authToken);
            prepStatement.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error deleting authData");
        }
    }

    public void updateGame(GameData g) throws DataAccessException{
        Connection conn = getConnection();
        var statement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, ChessGame = ? WHERE gameID = ?";
        try(var prepStatement = conn.prepareStatement(statement)){
            prepStatement.setString(1, g.whiteUsername());
            prepStatement.setString(2, g.blackUsername());
            prepStatement.setString(3, gson.toJson(g.game()));
            prepStatement.setInt(4, g.gameID());
            prepStatement.executeUpdate();
        }catch (SQLException e){
            throw new DataAccessException("Error updating GameData");
        }
    }

    public enum DataType{
        GAMEDATA,
        AUTHDATA,
        USERDATA
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatabaseManager that = (DatabaseManager) o;
        try{
            boolean a = Objects.equals(getAllGameData(),that.getAllGameData());
            boolean b = Objects.equals(getAllAuthData(),that.getAllAuthData());
            boolean c = Objects.equals(getAllUserData(),that.getAllUserData());
            return a && b && c;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gson);
    }
}
