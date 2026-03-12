package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DAOUnitTests {
    private AuthDAO a;
    private GameDAO g;
    private UserDAO u;
    private DatabaseManager db;

    public DAOUnitTests() throws DataAccessException{


    }

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




    //try{}catch(DataAccessException e){}

}
