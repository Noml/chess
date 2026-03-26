package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import client.results.*;
import client.requests.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static String nameEx;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);
        nameEx = "Joenathan";
    }

    @AfterAll
    static void stopServer() throws Exception {
        facade.clear();
        server.stop();
    }

    @BeforeEach
    void beforeEach() throws Exception{
        facade.clear();
    }

    @Test
    public void clearEmpty(){
        try{
            facade.clear();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void registerPos(){
        try{
            facade.register(new RegisterRequest(nameEx,"password1","email1"));
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void registerNeg(){
        try{
            facade.register(new RegisterRequest(nameEx,null,"email1"));
            Assertions.fail();
        }catch (Exception e){
            Assertions.assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    public void loginPos(){
        try{
            facade.register(new RegisterRequest(nameEx,"password1","email1"));
            LoginResult result1 = facade.login(new LoginRequest(nameEx,"password1"));
            Assertions.assertEquals(nameEx,result1.username());
            Assertions.assertEquals(String.class,result1.authToken().getClass());
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void loginNeg(){
        try{
            facade.register(new RegisterRequest(nameEx,"password1","email1"));
            facade.login(new LoginRequest(nameEx+"invalid","password1"));
            Assertions.fail();
        }catch (Exception e){
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }

    @Test
    public void logoutPos(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            facade.logout(new LogoutRequest(result.authToken()));
        }catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void logoutNeg(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            facade.logout(new LogoutRequest(result.authToken()+"invalid"));
            Assertions.fail();
        }catch (Exception e) {
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }

    @Test
    public void createGamePos(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            CreateGameResult result1 = facade.createGame(result.authToken(),"Game1");
            Assertions.assertEquals(1,result1.gameID());
        }catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void createGameNeg(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            facade.createGame(result.authToken()+"invalid","Game1");
            Assertions.fail();
        }catch (Exception e) {
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }



    @Test
    public void joinGamePos(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            CreateGameResult result1 = facade.createGame(result.authToken(),"Game1");
            GameData result2 = facade.joinGame(new JoinRequest("WHITE",result1.gameID()),result.authToken()).gameData();
            Assertions.assertEquals(1, result2.gameID());
            Assertions.assertEquals(result2.whiteUsername(),nameEx);
            Assertions.assertNull(result2.blackUsername());
            Assertions.assertEquals("Game1", result2.gameName());
        }catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    public void joinGameNeg(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            CreateGameResult result1 = facade.createGame(result.authToken(),"Game1");
            facade.joinGame(new JoinRequest("WHITE",result1.gameID()),result.authToken()+"invalid");
            Assertions.fail();
        }catch (Exception e) {
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }

    @Test
    public void listGamesPos(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            CreateGameResult result1 = facade.createGame(result.authToken(),"Game1");
            facade.joinGame(new JoinRequest("WHITE",result1.gameID()),result.authToken());
            var result3 = facade.listGames(result.authToken());
            Assertions.assertFalse(result3.isEmpty());
        }catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void listGamesNeg(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            CreateGameResult result1 = facade.createGame(result.authToken(),"Game1");
            facade.joinGame(new JoinRequest("WHITE",result1.gameID()),result.authToken());
            facade.listGames(result.authToken()+"invalid");
            Assertions.fail();
        }catch (Exception e) {
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }

}
