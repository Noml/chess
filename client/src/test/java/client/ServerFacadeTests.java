package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;


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
    static void stopServer() {
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
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void registerNeg(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,null,"email1"));
            Assertions.fail();
        }catch (Exception e){
            Assertions.assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    public void loginPos(){
        try{
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
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
            RegisterResult result = facade.register(new RegisterRequest(nameEx,"password1","email1"));
            LoginResult result1 = facade.login(new LoginRequest(nameEx+"invalid","password1"));
            Assertions.fail();
        }catch (Exception e){
            Assertions.assertEquals("Error: unauthorized",e.getMessage());
        }
    }
}
