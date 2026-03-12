package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DAOUnitTests {
    private AuthDAO a;
    private GameDAO g;
    private UserDAO u;

    @BeforeEach
    public void setUp() throws DataAccessException{
        DatabaseManager db = new DatabaseManager();
        a = new AuthDAO(db);
        g = new GameDAO(db);
        u = new UserDAO(db);
        db.deleteData(DatabaseManager.DataType.AUTHDATA);
        db.deleteData(DatabaseManager.DataType.USERDATA);
        db.deleteData(DatabaseManager.DataType.GAMEDATA);
    }

    @Test
    public void createUserPos(){
        UserData userData = new UserData("Joenathan","12345","na@gmail.com");
        try{
            u.createUser(userData);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void createUserNeg(){
        UserData userData = new UserData("Joenathan","12345",null);
        try{
            u.createUser(userData);
            Assertions.fail();
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error adding UserData",e.getMessage());
        }
    }

    @Test
    public void getUserPos(){
        UserData userData = new UserData("Joenathan","12345","na@gmail.com");
        try{
            u.createUser(userData);
            UserData result = u.getUserByUsername(userData.username());
            Assertions.assertEquals(userData.username(),result.username());
            Assertions.assertNotNull(result.password());
            Assertions.assertEquals(userData.email(),result.email());
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void getUserNeg(){
        UserData userData = new UserData("Joenathan","12345","na@gmail.com");
        try{
            u.createUser(userData);
            UserData result = u.getUserByUsername("");
            Assertions.assertNull(result);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void clearUserData(){
        UserData userData = new UserData("Joenathan","12345","na@gmail.com");
        try{
            u.createUser(userData);
            u.clearUserData();
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void addAuthPos(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void addAuthNeg(){
        try{
            a.addAuthData(null);
            Assertions.fail();
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: null input",e.getMessage());
        }
    }

    @Test
    public void getAuthPos(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
            AuthData res = a.getAuthData(authData.authToken());
            Assertions.assertEquals(authData,res);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void getAuthNeg(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
            AuthData res = a.getAuthData("fakeAuthToken");
            Assertions.assertNull(res);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void deleteAuthPos(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
            AuthData res = a.getAuthData(authData.authToken());
            Assertions.assertNotNull(res);
            a.deleteAuth(authData);
            res = a.getAuthData(authData.authToken());
            Assertions.assertNull(res);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void deleteAuthNeg(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
            a.deleteAuth(null);
            Assertions.fail();
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: null input",e.getMessage());
        }
    }

    @Test
    public void clearAllAuthData(){
        AuthData authData = new AuthData("demoAuthToken", "Joenathan");
        try{
            a.addAuthData(authData);
            AuthData res = a.getAuthData(authData.authToken());
            Assertions.assertNotNull(res);
            a.clearAuthData();
            res = a.getAuthData(authData.authToken());
            Assertions.assertNull(res);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void getIDPos(){
        try{
            int x = g.getNewID();
            Assertions.assertEquals(1, x);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void addGamePos(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void addGameNeg(){
        try{
            g.addGameData(null);
            Assertions.fail();
        }catch(DataAccessException e){
            Assertions.assertEquals("Error: null input",e.getMessage());
        }
    }

    @Test
    public void getIDPos2(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            int x = g.getNewID();
            Assertions.assertNotEquals(0, x);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void listGamesPos(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            var x = g.getAllGameData();
            Assertions.assertFalse(x.isEmpty());
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void listGamesNeg(){
        try{
            var x = g.getAllGameData();
            Assertions.assertTrue(x.isEmpty());
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void findGamePos(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            GameData gameData1 = g.findGame(1);
            Assertions.assertEquals(gameData1,gameData);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void findGameNeg(){
        try{
            GameData gameData1 = g.findGame(1);
            Assertions.assertNull(gameData1);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void addPlayerPos(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            GameData gameData1 = g.addPlayer(1,"WHITE","Joenathan");
            GameData expected = new GameData(1,"Joenathan",null,"g1",new ChessGame());
            Assertions.assertEquals(expected, gameData1);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void addPlayerNeg(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            GameData gameData1 = g.addPlayer(1,"white","Joenathan");
            Assertions.assertNull(gameData1);
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }

    @Test
    public void clearGameData(){
        try{
            GameData gameData = new GameData(1,null,null,"g1",new ChessGame());
            g.addGameData(gameData);
            var x = g.getAllGameData();
            Assertions.assertFalse(x.isEmpty());
            g.clearGameData();
            x = g.getAllGameData();
            Assertions.assertTrue(x.isEmpty());
        }catch(DataAccessException e){
            Assertions.fail();
        }
    }
    //try{}catch(DataAccessException e){}

}
