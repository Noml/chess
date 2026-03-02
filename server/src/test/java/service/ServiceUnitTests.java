package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Database;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.CreateGameResult;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.ArrayList;

public class ServiceUnitTests {
    private Service service;
    private Database db;

    @BeforeEach
    public void setUp(){
        db = new Database();
        service = new Service(db);
    }

    @Test
    public void testClear(){
        Database cleared = new Database();
        service.clear();
        Assertions.assertEquals(cleared, db);
    }

    @Test
    public void addUser(){//register positive case
        UserService s =  new UserService(service);
        RegisterRequest  req = new RegisterRequest("Joenathan","12345","na@gmail.com");
        RegisterResult result = s.register(req);
        RegisterResult expected = new RegisterResult("Joenathan","a1u2t3h4T5o6k7e8nEX");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.username(),result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void tryAddUserTwice(){//register negative case
        UserService s = new UserService(service);
        RegisterRequest  req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        RegisterRequest req2 = new RegisterRequest("Joenathan","12345","na2@gmail.com");
        RegisterResult result2 = s.register(req2);
        RegisterResult expected = new RegisterResult("Error","Error: already taken");
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(expected.username(),result2.username());
        Assertions.assertEquals(expected.authToken(),result2.authToken());
    }

    @Test
    public void loginUser(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginResult lRes = s.login(lReq);
        LoginResult expected = new LoginResult("Joenathan","a1u2t3h4T5o6k7e8nEX");
        Assertions.assertNotNull(lRes);
        Assertions.assertEquals(expected.username(),lRes.username());
        Assertions.assertNotNull(lRes.authToken());
    }

    @Test
    public void loginInvalidPassword(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","123456789");
        LoginResult lRes = s.login(lReq);
        LoginResult expected = new LoginResult("Error","Error: unauthorized");
        Assertions.assertNotNull(lRes);
        Assertions.assertEquals(expected.username(),lRes.username());
        Assertions.assertEquals(expected.authToken(),lRes.authToken());
    }

    @Test
    public void logoutUser(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginResult lRes = s.login(lReq);
        LogoutRequest logoutRequest = new LogoutRequest(lRes.authToken());
        boolean b = s.logout(logoutRequest);
        Assertions.assertTrue(b);
    }

    @Test
    public void logoutInexistantUser(){
        UserService s = new UserService(service);
        LogoutRequest logoutRequest = new LogoutRequest("fakeAuth");
        boolean b = s.logout(logoutRequest);
        Assertions.assertFalse(b);
    }

    @Test
    public void createGame(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginResult lRes = s.login(lReq);
        GameService g = new GameService(service);
        try {
            CreateGameResult c = g.createGame(lRes.authToken(),"game1");
            CreateGameResult expected = new CreateGameResult(1);
            Assertions.assertEquals(c,expected);
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void tryCreateInvalidGame(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        s.register(req);
        s.login(lReq);
        GameService g = new GameService(service);
        DataAccessException expected = new DataAccessException("Error: unauthorized");
        try {
            g.createGame("fakeAuth","game1");
        }catch (Exception e){
            Assertions.assertEquals(e.getMessage(),expected.getMessage());
        }
    }

    @Test
    public void joinGamePos(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        RegisterRequest req2 = new RegisterRequest("Ambar","4321","an@gmail.com");
        s.register(req);
        s.register(req2);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginRequest lReq2 = new LoginRequest("Ambar","4321");
        LoginResult lRes = s.login(lReq);
        LoginResult lRes2 = s.login(lReq2);
        GameService g = new GameService(service);
        try {
            CreateGameResult c = g.createGame(lRes.authToken(),"game1");
            g.joinGame(lRes.authToken(),"WHITE",c.gameID());
            GameData gameData1 = g.joinGame(lRes2.authToken(),"BLACK",c.gameID());
            GameData expected = new GameData(1,"Joenathan","Ambar","game1",new ChessGame());
            Assertions.assertEquals(gameData1.gameID(),expected.gameID());
            Assertions.assertEquals(gameData1.whiteUsername(),expected.whiteUsername());
            Assertions.assertEquals(gameData1.blackUsername(),expected.blackUsername());
            Assertions.assertEquals(gameData1.gameName(),expected.gameName());
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void joinGameNeg(){
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        RegisterRequest req2 = new RegisterRequest("Ambar","4321","an@gmail.com");
        s.register(req);
        s.register(req2);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginRequest lReq2 = new LoginRequest("Ambar","4321");
        LoginResult lRes = s.login(lReq);
        LoginResult lRes2 = s.login(lReq2);
        GameService g = new GameService(service);
        try {
            CreateGameResult c = g.createGame(lRes.authToken(),"game1");
            g.joinGame(lRes.authToken(),"WHITE",c.gameID());
            g.joinGame(lRes2.authToken(),"WHITE",c.gameID());
        }catch (Exception e){
            String message = e.getMessage();
            String expectedMessage = "Error: already taken";
            Assertions.assertEquals(expectedMessage, message);
        }
    }


    @Test
    public void clearNoData(){
        boolean b = service.clear();
        Assertions.assertTrue(b);
    }

    @Test
    public void clearData(){
        UserService s = new UserService(service);
        GameService g = new GameService(service);
        RegisterRequest  req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginResult lRes =  s.login(lReq);
        try {
            CreateGameResult c = g.createGame(lRes.authToken(),"game1");
            CreateGameResult expected = new CreateGameResult(1);
            Assertions.assertEquals(c,expected);
        }catch (Exception e){
            Assertions.fail();
        }
        boolean b = s.clear();
        Assertions.assertTrue(b);
    }

    @Test
    public void listGamesPosEx() {
        UserService s = new UserService(service);
        RegisterRequest req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        RegisterRequest req2 = new RegisterRequest("Ambar","4321","an@gmail.com");
        s.register(req);
        s.register(req2);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginRequest lReq2 = new LoginRequest("Ambar","4321");
        LoginResult lRes = s.login(lReq);
        LoginResult lRes2 = s.login(lReq2);
        GameService g = new GameService(service);
        try {
            CreateGameResult c = g.createGame(lRes.authToken(), "game1");
            CreateGameResult c2 = g.createGame(lRes.authToken(), "game2");
            g.createGame(lRes.authToken(), "game3");
            g.joinGame(lRes.authToken(),"WHITE",c.gameID());
            g.joinGame(lRes2.authToken(),"BLACK",c.gameID());
            g.joinGame(lRes.authToken(),"BLACK",c2.gameID());
            g.joinGame(lRes2.authToken(),"WHITE",c2.gameID());
            ArrayList<GameData> listed = g.listGames(lRes.authToken());
            ArrayList<GameData> expected = new ArrayList<>();
            expected.add(new GameData(1,"Joenathan","Ambar","game1",new ChessGame()));
            expected.add(new GameData(2,"Ambar","Joenathan","game2",new ChessGame()));
            expected.add(new GameData(3, null, null, "game3", new ChessGame()));
            for (int i = 0; i < listed.size(); i++) {
                var gData = listed.get(i);
                var expectedData = expected.get(i);
                Assertions.assertEquals(gData.gameID(), expectedData.gameID());
                Assertions.assertEquals(gData.gameName(), expectedData.gameName());
                Assertions.assertEquals(gData.whiteUsername(),expectedData.whiteUsername());
                Assertions.assertEquals(gData.blackUsername(),expectedData.blackUsername());
            }
        }catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void listGamesNegEx(){
        UserService s = new UserService(service);
        GameService g = new GameService(service);
        RegisterRequest  req = new RegisterRequest("Joenathan","1234","na@gmail.com");
        s.register(req);
        LoginRequest lReq = new LoginRequest("Joenathan","1234");
        LoginResult lRes =  s.login(lReq);
        try {
            g.createGame(lRes.authToken(),"game1");
            g.createGame(lRes.authToken(),"game2");
            g.createGame(lRes.authToken(),"game3");
            g.listGames("invalidAuth");

        }catch (Exception e){
            String expectedMessage = "Error: unauthorized";
            String message = e.getMessage();
            Assertions.assertEquals(expectedMessage, message);
        }
    }
}
